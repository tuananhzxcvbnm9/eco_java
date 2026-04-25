export type Product = {
  id: string;
  slug: string;
  name: string;
  brand: string;
  price: number;
  rating: number;
  image: string;
  category: string;
  isFeatured?: boolean;
};

export const products: Product[] = [
  {
    id: 'p1',
    slug: 'nike-air-zoom-alpha',
    name: 'Nike Air Zoom Alpha',
    brand: 'Nike',
    price: 2290000,
    rating: 4.8,
    image: 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=1200',
    category: 'Sneakers',
    isFeatured: true
  },
  {
    id: 'p2',
    slug: 'adidas-ultra-lite',
    name: 'Adidas Ultra Lite',
    brand: 'Adidas',
    price: 1990000,
    rating: 4.6,
    image: 'https://images.unsplash.com/photo-1514989940723-e8e51635b782?w=1200',
    category: 'Running'
  },
  {
    id: 'p3',
    slug: 'puma-street-v2',
    name: 'Puma Street V2',
    brand: 'Puma',
    price: 1590000,
    rating: 4.4,
    image: 'https://images.unsplash.com/photo-1600269452121-4f2416e55c28?w=1200',
    category: 'Lifestyle'
  }
];

export const currency = (value: number) =>
  new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
