package com.exemplo.controlemesas.services;

import com.exemplo.controlemesas.dto.ComandaResumoDTO;
import com.exemplo.controlemesas.model.*;
import com.exemplo.controlemesas.model.enums.StatusComanda;
import com.exemplo.controlemesas.model.enums.StatusItemComanda;
import com.exemplo.controlemesas.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComandaServiceTest {

    @Mock
    private ComandaRepository comandaRepository;
    @Mock
    private MesaRepository mesaRepository;
    @Mock
    private ItemComandaRepository itemComandaRepository;
    @Mock
    private ComandaResumoRepository comandaResumoRepository;
    @Mock
    private NfeService nfeService;

    private ComandaService comandaService;

    @BeforeEach
    void setup() {
        // nothing: each test will create its own ComandaService instance to allow per-test overrides
    }

    @Test
    void fecharComanda_success_emitsNfeWithResumoId() throws Exception {
        // arrange
        Comanda comanda = new Comanda();
        comanda.setId(10L);
        comanda.setStatus(StatusComanda.ABERTA);

        Produto produto = new Produto();
        produto.setId(5L);
        produto.setNome("P");

        ItemComanda item = new ItemComanda();
        item.setId(1L);
        item.setQuantidade(2);
        item.setPrecoVenda(BigDecimal.valueOf(5.00));
        item.setTotal(BigDecimal.valueOf(10.00));
        item.setStatus(StatusItemComanda.PENDENTE);
        item.setProduto(produto);

        when(comandaRepository.findById(anyLong())).thenReturn(Optional.of(comanda));
        when(itemComandaRepository.findByComandaId(anyLong())).thenReturn(Collections.singletonList(item));
        org.mockito.Mockito.lenient().when(comandaResumoRepository.save(any(ComandaResumo.class))).thenAnswer(inv -> {
            ComandaResumo r = inv.getArgument(0);
            r.setId(99L);
            return r;
        });
        org.mockito.Mockito.lenient().when(comandaResumoRepository.saveAndFlush(any(ComandaResumo.class))).thenAnswer(inv -> {
            ComandaResumo r = inv.getArgument(0);
            r.setId(99L);
            return r;
        });

        // create service that delegates to mock nfeService (so we can verify mock interaction)
        comandaService = new ComandaService(comandaRepository, mesaRepository, itemComandaRepository, comandaResumoRepository, nfeService) {
            @Override
            protected void emitNfe(Long resumoId) throws IOException {
                // delegate to mocked nfeService
                if (resumoId != null) nfeService.emitir(resumoId);
            }
        };

        // act
        ComandaResumoDTO dto = comandaService.fecharComanda(10L, "Cliente", "Obs");

        // assert
        assertNotNull(dto);
        assertEquals(99L, dto.getId());
        verify(nfeService, times(1)).emitir(99L);
        verify(comandaResumoRepository, atLeastOnce()).saveAndFlush(any(ComandaResumo.class));
    }

    @Test
    void fecharComanda_whenNfeThrowsIOException_marksResumoErro_andReturnsResumo() throws Exception {
        // arrange
        Comanda comanda = new Comanda();
        comanda.setId(20L);
        comanda.setStatus(StatusComanda.ABERTA);

        Produto produto = new Produto();
        produto.setId(6L);

        ItemComanda item = new ItemComanda();
        item.setId(2L);
        item.setQuantidade(1);
        item.setPrecoVenda(BigDecimal.valueOf(3.00));
        item.setTotal(BigDecimal.valueOf(3.00));
        item.setStatus(StatusItemComanda.PENDENTE);
        item.setProduto(produto);

        when(comandaRepository.findById(anyLong())).thenReturn(Optional.of(comanda));
        when(itemComandaRepository.findByComandaId(anyLong())).thenReturn(Collections.singletonList(item));
        org.mockito.Mockito.lenient().when(comandaResumoRepository.save(any(ComandaResumo.class))).thenAnswer(inv -> {
            ComandaResumo r = inv.getArgument(0);
            r.setId(55L);
            return r;
        });
        org.mockito.Mockito.lenient().when(comandaResumoRepository.saveAndFlush(any(ComandaResumo.class))).thenAnswer(inv -> {
            ComandaResumo r = inv.getArgument(0);
            r.setId(55L);
            return r;
        });

        // create service that throws IOException when emitting
        comandaService = new ComandaService(comandaRepository, mesaRepository, itemComandaRepository, comandaResumoRepository, nfeService) {
            @Override
            protected void emitNfe(Long resumoId) throws IOException {
                throw new IOException("socket error");
            }
        };

        // act
        ComandaResumoDTO dto = comandaService.fecharComanda(20L, "C", "O");

        // assert: no exception, resumo returned and marked as ERRO
        assertNotNull(dto);
        assertEquals(55L, dto.getId());
        assertEquals("ERRO", dto.getStatusSat());

        // verify resumo was persisted (id assigned)
        verify(comandaResumoRepository, atLeastOnce()).saveAndFlush(any(ComandaResumo.class));
    }
}