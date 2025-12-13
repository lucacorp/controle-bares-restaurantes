// src/components/EstoquePage.tsx
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import EstoqueList from "./EstoqueList";
import MovimentacaoList from "./MovimentacaoList";

export default function EstoquePage() {
  return (
    <div className="max-w-6xl mx-auto mt-10 px-4">
      <h1 className="text-3xl font-bold mb-6">Estoque</h1>

      <Tabs defaultValue="estoque" className="w-full">
        <TabsList className="mb-6">
          <TabsTrigger value="estoque">Estoque Atual</TabsTrigger>
          <TabsTrigger value="movimentacoes">Movimentações</TabsTrigger>
        </TabsList>

        <TabsContent value="estoque">
          <EstoqueList />
        </TabsContent>

        <TabsContent value="movimentacoes">
          <MovimentacaoList />
        </TabsContent>
      </Tabs>
    </div>
  );
}
