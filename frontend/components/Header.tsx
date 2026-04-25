import Link from 'next/link';

const links = [
  { href: '/', label: 'Home' },
  { href: '/products', label: 'Products' },
  { href: '/cart', label: 'Cart' },
  { href: '/orders', label: 'Orders' },
  { href: '/profile', label: 'Profile' },
  { href: '/admin', label: 'Admin' }
];

export function Header() {
  return (
    <header className="border-b bg-white sticky top-0 z-20">
      <div className="container nav">
        <Link href="/" className="logo">EcoCommerce</Link>
        <nav>
          <ul className="menu">
            {links.map((item) => (
              <li key={item.href}>
                <Link href={item.href}>{item.label}</Link>
              </li>
            ))}
          </ul>
        </nav>
      </div>
    </header>
  );
}
