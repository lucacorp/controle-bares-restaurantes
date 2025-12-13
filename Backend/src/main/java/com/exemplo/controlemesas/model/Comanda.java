package com.exemplo.controlemesas.model;

import com.exemplo.controlemesas.model.enums.StatusComanda;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "Comandas")
public class Comanda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mesa_id", nullable = false)
    private Mesa mesa;

    private Integer numeroMesa;
    private LocalDateTime dataAbertura;
    private LocalDateTime dataFechamento;
    private boolean ativa = true;

    @Version
    private Long version;

    // 🔹 CAMPOS ADICIONADOS
    @Enumerated(EnumType.STRING)
    private StatusComanda status; 

    @OneToMany(mappedBy = "comanda", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ItemComanda> itens = new ArrayList<>();

    // ===== Construtores =====
    public Comanda() {
        this.dataAbertura = LocalDateTime.now();
        this.ativa = true;
        this.status = StatusComanda.ABERTA; // corrected enum constant
    }

    public Comanda(Integer numeroMesa) {
        this.numeroMesa = numeroMesa;
        this.dataAbertura = LocalDateTime.now();
        this.ativa = true;
        this.status = StatusComanda.ABERTA; // corrected enum constant
    }
    
    // ===== MÉTODOS ADICIONADOS =====
    public StatusComanda getStatus() {
        return status;
    }

    public void setStatus(StatusComanda status) {
        this.status = status;
        // keep 'ativa' in sync with status
        this.ativa = (status == StatusComanda.ABERTA);
        // set dataFechamento automatically when comanda is closed or cancelled
        if (status == StatusComanda.FECHADA || status == StatusComanda.CANCELADA) {
            if (this.dataFechamento == null) {
                this.dataFechamento = LocalDateTime.now();
            }
        } else {
            this.dataFechamento = null;
        }
    }

    // Convenience lifecycle methods to keep fields consistent
    public void abrir() {
        this.status = StatusComanda.ABERTA;
        this.ativa = true;
        if (this.dataAbertura == null) this.dataAbertura = LocalDateTime.now();
        this.dataFechamento = null;
    }

    public void fechar() {
        this.status = StatusComanda.FECHADA;
        this.ativa = false;
        this.dataFechamento = LocalDateTime.now();
    }

    public void cancelar() {
        this.status = StatusComanda.CANCELADA;
        this.ativa = false;
        this.dataFechamento = LocalDateTime.now();
    }

    // ===== Métodos utilitários =====
    public void adicionarItem(ItemComanda item) {
        if (item == null) return;
        // avoid duplicates: if item has id, check by id; otherwise check by reference
        for (ItemComanda it : itens) {
            if (item.getId() != null && it.getId() != null) {
                if (item.getId().equals(it.getId())) return;
            } else if (it == item) {
                return;
            }
        }
        itens.add(item);
        item.setComanda(this);
    }

    public void removerItem(ItemComanda item) {
        if (item == null) return;
        // remove by id if available, otherwise by reference
        ItemComanda toRemove = null;
        for (ItemComanda it : itens) {
            if (item.getId() != null && it.getId() != null) {
                if (item.getId().equals(it.getId())) {
                    toRemove = it;
                    break;
                }
            } else if (it == item) {
                toRemove = it;
                break;
            }
        }
        if (toRemove != null) {
            itens.remove(toRemove);
            toRemove.setComanda(null);
        }
    }

    // ===== Getters e Setters =====
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public Mesa getMesa() {
        return mesa;
    }

    public void setMesa(Mesa mesa) {
        if (mesa == null) {
            throw new IllegalArgumentException("mesa cannot be null");
        }
        this.mesa = mesa;
        // manter numeroMesa sincronizado quando houver mesa
        if (mesa != null && mesa.getNumero() != null) {
            this.numeroMesa = mesa.getNumero();
        } else {
            this.numeroMesa = null;
        }
    }

    public Integer getNumeroMesa() {
        return numeroMesa;
    }

    public void setNumeroMesa(Integer numeroMesa) {
        this.numeroMesa = numeroMesa;
    }

    public LocalDateTime getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(LocalDateTime dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public LocalDateTime getDataFechamento() {
        return dataFechamento;
    }

    public void setDataFechamento(LocalDateTime dataFechamento) {
        this.dataFechamento = dataFechamento;
    }

    public boolean isAtiva() {
        return ativa;
    }

    public void setAtiva(boolean ativa) {
        this.ativa = ativa;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public List<ItemComanda> getItens() {
        return Collections.unmodifiableList(itens);
    }

    public void setItens(List<ItemComanda> novosItens) {
        // remover back-references dos itens atuais
        List<ItemComanda> copia = new ArrayList<>(this.itens);
        for (ItemComanda it : copia) {
            removerItem(it);
        }
        // adicionar novos itens usando adicionarItem para manter backref
        if (novosItens != null) {
            for (ItemComanda it : novosItens) {
                adicionarItem(it);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Comanda)) return false;
        Comanda comanda = (Comanda) o;
        return id != null && Objects.equals(id, comanda.id);
    }

    @Override
    public int hashCode() {
        // use id when available to satisfy equals/hashCode contract
        return Objects.hashCode(id);
    }
}