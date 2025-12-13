// src/components/ProductManager.tsx

import { useState } from 'react';
import ProductForm from './ProductForm';
import ProductList from './ProductList';

export default function ProductManager() {
  const [selectedTab, setSelectedTab] = useState<'form' | 'list'>('form');
  const [selectedId, setSelectedId] = useState<number | null>(null);
  const [refreshCounter, setRefreshCounter] = useState(0);

  const handleEdit = (id: number) => {
    setSelectedId(id);
    setSelectedTab('form');
  };

  const handleSave = () => {
    setSelectedId(null);
    setSelectedTab('list');
    setRefreshCounter((prev) => prev + 1); // força recarregar lista
  };

  const handleNewProduct = () => {
    setSelectedId(null);
    setSelectedTab('form');
  };

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold mb-6 text-gray-800">Gerenciamento de Produtos</h1>

      {/* Abas */}
      <div className="flex mb-4 border-b">
        <button
          className={`px-4 py-2 ${
            selectedTab === 'form' ? 'border-b-2 border-blue-600 font-semibold' : 'text-gray-600'
          }`}
          onClick={() => setSelectedTab('form')}
        >
          Cadastro
        </button>
        <button
          className={`px-4 py-2 ${
            selectedTab === 'list' ? 'border-b-2 border-blue-600 font-semibold' : 'text-gray-600'
          }`}
          onClick={() => setSelectedTab('list')}
        >
          Lista
        </button>
        <button
          className="ml-auto px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
          onClick={handleNewProduct}
        >
          Novo Produto
        </button>
      </div>

      {/* Conteúdo */}
      <div className="border p-4 rounded bg-white shadow">
        {selectedTab === 'form' ? (
          <ProductForm id={selectedId} onSave={handleSave} />
        ) : (
          <ProductList onEdit={handleEdit} refreshTrigger={refreshCounter} />
        )}
      </div>
    </div>
  );
}
