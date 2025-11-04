// src/components/ui/card.tsx
import * as React from "react";

interface CardProps extends React.HTMLAttributes<HTMLDivElement> {}

const Card = React.forwardRef<HTMLDivElement, CardProps>(
  ({ className = "", ...props }, ref) => (
    <div
      ref={ref}
      className={`rounded-2xl border bg-white shadow-sm p-4 ${className}`}
      {...props}
    />
  )
);
Card.displayName = "Card";

const CardContent = ({ className = "", ...props }: CardProps) => (
  <div className={`p-2 ${className}`} {...props} />
);
CardContent.displayName = "CardContent";

export { Card, CardContent };
