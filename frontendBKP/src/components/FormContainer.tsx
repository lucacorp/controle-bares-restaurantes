// FormContainer.tsx

interface FormContainerProps {
  title: string;
  children: React.ReactNode;
  fullHeight?: boolean; // opcional → se quiser centralizar como tela cheia (ex: login)
}

export default function FormContainer({ title, children, fullHeight = false }: FormContainerProps) {
  return (
    <div
      className={`${
        fullHeight ? 'flex justify-center items-center min-h-screen bg-gray-100 px-4' : ''
      }`}
    >
      <div className="bg-white shadow-md rounded-2xl p-6 w-full">
        <h2 className="text-xl font-bold mb-4 text-gray-800">{title}</h2>
        {children}
      </div>
    </div>
  );
}
