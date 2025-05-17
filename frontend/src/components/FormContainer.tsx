// FormContainer.tsx
export default function FormContainer({ title, children }: FormContainerProps) {
  return (
    <div className="flex justify-center items-center min-h-screen bg-gray-100 px-4">
      <div className="bg-white shadow-md rounded-2xl p-8 w-full max-w-md">
        <h2 className="text-2xl font-bold mb-6 text-center text-gray-800">{title}</h2>
        {children}
      </div>
    </div>
  );
}
