import type { Metadata } from 'next';
import './globals.css';
import { Header } from '@/components/Header';

export const metadata: Metadata = {
  title: 'EcoCommerce',
  description: 'Storefront + Admin cho dự án E-commerce production-ready',
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="vi">
      <body>
        <Header />
        <main className="container">{children}</main>
      </body>
    </html>
  );
}
