# Frontend API Integration Guide
## HangHae Plus E-commerce Spring Application

*English documentation with Korean context and examples*

### Table of Contents
1. [API Overview & Architecture](#api-overview--architecture)
2. [API Endpoints Reference](#api-endpoints-reference)
3. [Frontend Integration Patterns](#frontend-integration-patterns)
4. [Error Handling](#error-handling)
5. [State Management](#state-management)
6. [Sample Code Examples](#sample-code-examples)
7. [Testing Strategies](#testing-strategies)
8. [Performance Optimization](#performance-optimization)

---

## API Overview & Architecture

### Backend Architecture
The backend follows **Clean Architecture (Hexagonal Architecture)** with domain-driven design:
- **Domain Layer**: Core business logic (Product, Order, Point, Coupon)
- **Application Layer**: Use cases and business orchestration
- **Adapter Layer**: Web controllers (REST API) and persistence

### API Design Principles
- **RESTful endpoints** following standard HTTP methods
- **Consistent response structure** with `ApiResponse<T>` wrapper
- **Domain-based routing** (`/products`, `/orders`, `/points`, `/coupons`)
- **Error handling** with appropriate HTTP status codes

### Base Configuration
```javascript
// API Configuration
const API_BASE_URL = 'http://localhost:8080'

// Default headers for all requests
const DEFAULT_HEADERS = {
  'Content-Type': 'application/json',
  'Accept': 'application/json'
}
```

---

## API Endpoints Reference

### 📦 Product APIs

#### Get Product List
```http
GET /products
```
**Response:**
```typescript
interface ProductListResponse {
  success: boolean;
  data: Array<{
    id: number;
    productName: string;
    amount: number;  // Price in Korean Won
    quantity: number; // Stock quantity
  }>;
  error?: string;
}
```

#### Get Single Product
```http
GET /products/{id}
```
**Response:**
```typescript
interface ProductResponse {
  success: boolean;
  data: {
    id: number;
    productName: string;
    amount: number;
    quantity: number;
  };
  error?: string;
}
```

### 🛒 Order APIs

#### Place Order (주문 생성)
```http
POST /orders/place
```
**Request Body:**
```typescript
interface OrderRequest {
  userId: number;
  productId: number;
  quantity: number;
  amount: number; // Total amount
}
```
**Response:**
```typescript
interface OrderResponse {
  success: boolean;
  data: {
    orderId: number;
    userId: number;
    product: number; // productId
    quantity: number;
    price: number;
    orderTime?: string;
  };
  error?: string;
}
```

#### Order and Pay (주문 + 결제)
```http
POST /orders/order-and-pay
```
**Request Body:** Same as Place Order
**Response:** Same as Place Order (but payment is processed automatically)

### 💰 Point APIs

#### Charge Points (포인트 충전)
```http
POST /points/charge
```
**Request Body:**
```typescript
interface PointChargeRequest {
  userId: number;
  amount: number; // Amount to charge
}
```
**Response:**
```typescript
interface PointResponse {
  success: boolean;
  data: {
    balance: number; // New balance after charge
    userId: number;
    timestamp: string; // ISO datetime string
  };
  error?: string;
}
```

#### Get Point Balance (포인트 조회)
```http
GET /points/balance/{userId}
```
**Response:** Same as charge response

### 🎟️ Coupon APIs

#### Issue Coupon (쿠폰 발급)
```http
POST /coupons/{couponId}/issue/{userId}
```
**Response:**
```typescript
interface CouponIssueResponse {
  success: boolean;
  data: {
    couponId: number;
    userId: number;
    requestId: string; // For tracking async processing
    status: 'REQUESTED' | 'ISSUED' | 'FAILED';
  };
  error?: string;
}
```
*Note: Coupon issuance is processed asynchronously using Kafka*

---

## Frontend Integration Patterns

### 1. API Client Architecture

#### Base HTTP Client (Using Axios)
```typescript
// api/httpClient.ts
import axios, { AxiosInstance, AxiosResponse } from 'axios';

export interface ApiResponse<T> {
  success: boolean;
  data: T;
  error?: string;
}

class HttpClient {
  private client: AxiosInstance;

  constructor(baseURL: string = 'http://localhost:8080') {
    this.client = axios.create({
      baseURL,
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      },
      timeout: 10000 // 10 second timeout
    });

    this.setupInterceptors();
  }

  private setupInterceptors() {
    // Request interceptor for adding auth tokens
    this.client.interceptors.request.use(
      (config) => {
        // Add authentication token if available
        const token = localStorage.getItem('authToken');
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
      },
      (error) => Promise.reject(error)
    );

    // Response interceptor for error handling
    this.client.interceptors.response.use(
      (response: AxiosResponse<ApiResponse<any>>) => response,
      (error) => {
        console.error('API Error:', error.response?.data || error.message);
        return Promise.reject(error);
      }
    );
  }

  async get<T>(url: string): Promise<ApiResponse<T>> {
    const response = await this.client.get<ApiResponse<T>>(url);
    return response.data;
  }

  async post<T, U>(url: string, data: U): Promise<ApiResponse<T>> {
    const response = await this.client.post<ApiResponse<T>>(url, data);
    return response.data;
  }
}

export const httpClient = new HttpClient();
```

#### Domain-Specific API Services

```typescript
// api/productService.ts
import { httpClient, ApiResponse } from './httpClient';

export interface Product {
  id: number;
  productName: string;
  amount: number;
  quantity: number;
}

export class ProductService {
  static async getProductList(): Promise<Product[]> {
    const response = await httpClient.get<Product[]>('/products');
    if (!response.success) {
      throw new Error(response.error || '상품 목록을 불러올 수 없습니다');
    }
    return response.data;
  }

  static async getProduct(id: number): Promise<Product> {
    const response = await httpClient.get<Product>(`/products/${id}`);
    if (!response.success) {
      throw new Error(response.error || '상품을 불러올 수 없습니다');
    }
    return response.data;
  }
}
```

```typescript
// api/orderService.ts
export interface OrderRequest {
  userId: number;
  productId: number;
  quantity: number;
  amount: number;
}

export interface OrderResponse {
  orderId: number;
  userId: number;
  product: number;
  quantity: number;
  price: number;
  orderTime?: string;
}

export class OrderService {
  static async placeOrder(orderData: OrderRequest): Promise<OrderResponse> {
    const response = await httpClient.post<OrderResponse, OrderRequest>('/orders/place', orderData);
    if (!response.success) {
      throw new Error(response.error || '주문 처리에 실패했습니다');
    }
    return response.data;
  }

  static async orderAndPay(orderData: OrderRequest): Promise<OrderResponse> {
    const response = await httpClient.post<OrderResponse, OrderRequest>('/orders/order-and-pay', orderData);
    if (!response.success) {
      throw new Error(response.error || '주문 및 결제 처리에 실패했습니다');
    }
    return response.data;
  }
}
```

### 2. React Integration Examples

#### Product List Component with SWR
```tsx
// components/ProductList.tsx
import React from 'react';
import useSWR from 'swr';
import { ProductService, Product } from '../api/productService';

const ProductList: React.FC = () => {
  const { data: products, error, mutate } = useSWR<Product[]>(
    'products',
    ProductService.getProductList,
    {
      revalidateOnFocus: false,
      dedupingInterval: 60000, // Cache for 1 minute
    }
  );

  if (error) {
    return (
      <div className="alert alert-error">
        상품을 불러오는 중 오류가 발생했습니다: {error.message}
      </div>
    );
  }

  if (!products) {
    return <div className="loading">상품 목록을 불러오는 중...</div>;
  }

  return (
    <div className="product-grid">
      {products.map(product => (
        <ProductCard
          key={product.id}
          product={product}
          onPurchase={() => mutate()} // Refresh after purchase
        />
      ))}
    </div>
  );
};
```

#### Order Form with React Hook Form
```tsx
// components/OrderForm.tsx
import React from 'react';
import { useForm } from 'react-hook-form';
import { OrderService, OrderRequest } from '../api/orderService';

interface OrderFormProps {
  productId: number;
  productPrice: number;
  userId: number;
  onOrderComplete: (orderId: number) => void;
}

const OrderForm: React.FC<OrderFormProps> = ({ 
  productId, 
  productPrice, 
  userId, 
  onOrderComplete 
}) => {
  const { 
    register, 
    handleSubmit, 
    formState: { errors, isSubmitting }, 
    watch 
  } = useForm<{ quantity: number }>();

  const quantity = watch('quantity', 1);
  const totalAmount = quantity * productPrice;

  const onSubmit = async (data: { quantity: number }) => {
    try {
      const orderData: OrderRequest = {
        userId,
        productId,
        quantity: data.quantity,
        amount: totalAmount
      };

      const result = await OrderService.orderAndPay(orderData);
      onOrderComplete(result.orderId);
      
      // Show success message
      alert(`주문이 완료되었습니다! 주문번호: ${result.orderId}`);
    } catch (error) {
      alert(`주문 실패: ${error.message}`);
    }
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="order-form">
      <div className="form-group">
        <label htmlFor="quantity">수량</label>
        <input
          id="quantity"
          type="number"
          min="1"
          {...register('quantity', { 
            required: '수량을 입력해주세요',
            min: { value: 1, message: '최소 1개 이상 주문해주세요' }
          })}
        />
        {errors.quantity && (
          <span className="error">{errors.quantity.message}</span>
        )}
      </div>

      <div className="order-summary">
        <p>총 금액: {totalAmount.toLocaleString()}원</p>
      </div>

      <button 
        type="submit" 
        disabled={isSubmitting}
        className="btn btn-primary"
      >
        {isSubmitting ? '처리 중...' : '주문하기'}
      </button>
    </form>
  );
};
```

### 3. Vue Composition API Integration

```vue
<!-- components/ProductList.vue -->
<template>
  <div>
    <div v-if="loading" class="loading">상품을 불러오는 중...</div>
    
    <div v-else-if="error" class="alert alert-error">
      {{ error }}
    </div>
    
    <div v-else class="product-grid">
      <ProductCard 
        v-for="product in products" 
        :key="product.id"
        :product="product"
        @purchase="handlePurchase"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { ProductService, Product } from '../api/productService';

const products = ref<Product[]>([]);
const loading = ref(true);
const error = ref<string | null>(null);

const loadProducts = async () => {
  try {
    loading.value = true;
    error.value = null;
    products.value = await ProductService.getProductList();
  } catch (err) {
    error.value = err.message || '상품을 불러올 수 없습니다';
  } finally {
    loading.value = false;
  }
};

const handlePurchase = () => {
  // Refresh products after purchase
  loadProducts();
};

onMounted(() => {
  loadProducts();
});
</script>
```

---

## Error Handling

### 1. Error Types and Status Codes

The API returns consistent error responses:
- **400 Bad Request**: Invalid input data (IllegalArgumentException)
- **500 Internal Server Error**: Server-side errors
- **Success Response**: `success: true` with data
- **Error Response**: `success: false` with error message

### 2. Comprehensive Error Handling

```typescript
// utils/errorHandler.ts
export interface ApiError {
  status: number;
  message: string;
  details?: string;
}

export class ErrorHandler {
  static handleApiError(error: any): ApiError {
    if (error.response) {
      // Server responded with error status
      const { status, data } = error.response;
      return {
        status,
        message: data.error || '서버 오류가 발생했습니다',
        details: data.details
      };
    } else if (error.request) {
      // Network error
      return {
        status: 0,
        message: '네트워크 연결을 확인해주세요'
      };
    } else {
      // Other error
      return {
        status: -1,
        message: error.message || '알 수 없는 오류가 발생했습니다'
      };
    }
  }

  static getKoreanErrorMessage(error: ApiError): string {
    switch (error.status) {
      case 400:
        return '입력 정보를 확인해주세요';
      case 404:
        return '요청한 정보를 찾을 수 없습니다';
      case 500:
        return '서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요';
      case 0:
        return '인터넷 연결을 확인해주세요';
      default:
        return error.message;
    }
  }
}
```

### 3. Global Error Handling with React Error Boundary

```tsx
// components/ErrorBoundary.tsx
import React, { Component, ReactNode } from 'react';

interface ErrorBoundaryState {
  hasError: boolean;
  error?: Error;
}

export class ErrorBoundary extends Component<
  { children: ReactNode },
  ErrorBoundaryState
> {
  constructor(props: { children: ReactNode }) {
    super(props);
    this.state = { hasError: false };
  }

  static getDerivedStateFromError(error: Error): ErrorBoundaryState {
    return { hasError: true, error };
  }

  componentDidCatch(error: Error, errorInfo: any) {
    console.error('Error caught by boundary:', error, errorInfo);
    
    // Send error to monitoring service
    // sendErrorToMonitoring(error, errorInfo);
  }

  render() {
    if (this.state.hasError) {
      return (
        <div className="error-fallback">
          <h2>문제가 발생했습니다</h2>
          <p>페이지를 새로고침하거나 잠시 후 다시 시도해주세요.</p>
          <button 
            onClick={() => window.location.reload()}
            className="btn btn-primary"
          >
            페이지 새로고침
          </button>
        </div>
      );
    }

    return this.props.children;
  }
}
```

---

## State Management

### 1. Zustand Store for E-commerce State

```typescript
// store/ecommerceStore.ts
import { create } from 'zustand';
import { devtools } from 'zustand/middleware';
import { Product } from '../api/productService';
import { OrderResponse } from '../api/orderService';

interface CartItem extends Product {
  cartQuantity: number;
}

interface EcommerceState {
  // Products
  products: Product[];
  selectedProduct: Product | null;
  
  // Cart
  cart: CartItem[];
  
  // Orders
  orders: OrderResponse[];
  
  // Points
  userPoints: number;
  
  // Loading states
  loading: {
    products: boolean;
    cart: boolean;
    orders: boolean;
    points: boolean;
  };
  
  // Actions
  setProducts: (products: Product[]) => void;
  setSelectedProduct: (product: Product | null) => void;
  addToCart: (product: Product, quantity: number) => void;
  removeFromCart: (productId: number) => void;
  updateCartQuantity: (productId: number, quantity: number) => void;
  clearCart: () => void;
  addOrder: (order: OrderResponse) => void;
  setUserPoints: (points: number) => void;
  setLoading: (key: keyof EcommerceState['loading'], value: boolean) => void;
}

export const useEcommerceStore = create<EcommerceState>()(
  devtools(
    (set, get) => ({
      // Initial state
      products: [],
      selectedProduct: null,
      cart: [],
      orders: [],
      userPoints: 0,
      loading: {
        products: false,
        cart: false,
        orders: false,
        points: false,
      },

      // Actions
      setProducts: (products) => set({ products }),
      
      setSelectedProduct: (product) => set({ selectedProduct: product }),
      
      addToCart: (product, quantity) => {
        const { cart } = get();
        const existingItem = cart.find(item => item.id === product.id);
        
        if (existingItem) {
          set({
            cart: cart.map(item =>
              item.id === product.id
                ? { ...item, cartQuantity: item.cartQuantity + quantity }
                : item
            )
          });
        } else {
          set({
            cart: [...cart, { ...product, cartQuantity: quantity }]
          });
        }
      },
      
      removeFromCart: (productId) => {
        const { cart } = get();
        set({
          cart: cart.filter(item => item.id !== productId)
        });
      },
      
      updateCartQuantity: (productId, quantity) => {
        const { cart } = get();
        if (quantity <= 0) {
          get().removeFromCart(productId);
        } else {
          set({
            cart: cart.map(item =>
              item.id === productId
                ? { ...item, cartQuantity: quantity }
                : item
            )
          });
        }
      },
      
      clearCart: () => set({ cart: [] }),
      
      addOrder: (order) => {
        const { orders } = get();
        set({ orders: [order, ...orders] });
      },
      
      setUserPoints: (points) => set({ userPoints: points }),
      
      setLoading: (key, value) => {
        const { loading } = get();
        set({
          loading: { ...loading, [key]: value }
        });
      },
    }),
    {
      name: 'ecommerce-store',
    }
  )
);
```

### 2. React Query Integration for Server State

```typescript
// hooks/useProducts.ts
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { ProductService, Product } from '../api/productService';

export const useProducts = () => {
  return useQuery<Product[], Error>({
    queryKey: ['products'],
    queryFn: ProductService.getProductList,
    staleTime: 5 * 60 * 1000, // 5 minutes
    cacheTime: 10 * 60 * 1000, // 10 minutes
  });
};

export const useProduct = (id: number) => {
  return useQuery<Product, Error>({
    queryKey: ['product', id],
    queryFn: () => ProductService.getProduct(id),
    enabled: !!id,
  });
};

// hooks/useOrders.ts
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { OrderService, OrderRequest, OrderResponse } from '../api/orderService';

export const useCreateOrder = () => {
  const queryClient = useQueryClient();
  
  return useMutation<OrderResponse, Error, OrderRequest>({
    mutationFn: OrderService.orderAndPay,
    onSuccess: (data) => {
      // Invalidate and refetch related data
      queryClient.invalidateQueries({ queryKey: ['products'] });
      queryClient.invalidateQueries({ queryKey: ['userPoints'] });
      
      // Optimistically update user orders if we have that query
      queryClient.setQueryData(['userOrders'], (oldData: OrderResponse[] | undefined) => {
        return oldData ? [data, ...oldData] : [data];
      });
    },
  });
};
```

---

## Sample Code Examples

### 1. Complete E-commerce Page (React)

```tsx
// pages/EcommercePage.tsx
import React, { useState } from 'react';
import { useProducts, useCreateOrder } from '../hooks/useProducts';
import { useEcommerceStore } from '../store/ecommerceStore';
import { Product } from '../api/productService';

const EcommercePage: React.FC = () => {
  const { data: products, isLoading, error } = useProducts();
  const createOrderMutation = useCreateOrder();
  const { cart, addToCart, removeFromCart, clearCart } = useEcommerceStore();
  
  const [selectedQuantities, setSelectedQuantities] = useState<Record<number, number>>({});

  const handleAddToCart = (product: Product) => {
    const quantity = selectedQuantities[product.id] || 1;
    addToCart(product, quantity);
    setSelectedQuantities(prev => ({ ...prev, [product.id]: 1 }));
  };

  const handleCheckout = async () => {
    if (cart.length === 0) return;
    
    try {
      // For simplicity, create separate orders for each cart item
      // In a real app, you might want to create a single order with multiple items
      for (const item of cart) {
        await createOrderMutation.mutateAsync({
          userId: 1, // Get from auth context
          productId: item.id,
          quantity: item.cartQuantity,
          amount: item.amount * item.cartQuantity
        });
      }
      
      clearCart();
      alert('모든 주문이 완료되었습니다!');
    } catch (error) {
      alert(`주문 실패: ${error.message}`);
    }
  };

  const totalCartValue = cart.reduce(
    (total, item) => total + (item.amount * item.cartQuantity), 
    0
  );

  if (isLoading) return <div>상품을 불러오는 중...</div>;
  if (error) return <div>오류: {error.message}</div>;

  return (
    <div className="ecommerce-page">
      {/* Products Section */}
      <section className="products-section">
        <h2>상품 목록</h2>
        <div className="products-grid">
          {products?.map(product => (
            <div key={product.id} className="product-card">
              <h3>{product.productName}</h3>
              <p className="price">{product.amount.toLocaleString()}원</p>
              <p className="stock">재고: {product.quantity}개</p>
              
              <div className="quantity-selector">
                <input
                  type="number"
                  min="1"
                  max={product.quantity}
                  value={selectedQuantities[product.id] || 1}
                  onChange={(e) => setSelectedQuantities(prev => ({
                    ...prev,
                    [product.id]: parseInt(e.target.value)
                  }))}
                />
                <button 
                  onClick={() => handleAddToCart(product)}
                  disabled={product.quantity === 0}
                >
                  장바구니 담기
                </button>
              </div>
            </div>
          ))}
        </div>
      </section>

      {/* Cart Section */}
      <section className="cart-section">
        <h2>장바구니 ({cart.length}개 상품)</h2>
        {cart.length === 0 ? (
          <p>장바구니가 비어있습니다.</p>
        ) : (
          <>
            {cart.map(item => (
              <div key={item.id} className="cart-item">
                <span>{item.productName}</span>
                <span>{item.cartQuantity}개</span>
                <span>{(item.amount * item.cartQuantity).toLocaleString()}원</span>
                <button onClick={() => removeFromCart(item.id)}>제거</button>
              </div>
            ))}
            <div className="cart-total">
              <strong>총 금액: {totalCartValue.toLocaleString()}원</strong>
            </div>
            <button 
              onClick={handleCheckout}
              disabled={createOrderMutation.isPending}
              className="checkout-button"
            >
              {createOrderMutation.isPending ? '주문 처리 중...' : '주문하기'}
            </button>
          </>
        )}
      </section>
    </div>
  );
};

export default EcommercePage;
```

### 2. Point Management Component

```tsx
// components/PointManager.tsx
import React, { useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { PointService, PointChargeRequest } from '../api/pointService';

interface PointManagerProps {
  userId: number;
}

const PointManager: React.FC<PointManagerProps> = ({ userId }) => {
  const [chargeAmount, setChargeAmount] = useState<number>(0);
  const queryClient = useQueryClient();

  // Query for current point balance
  const { data: pointBalance, isLoading } = useQuery({
    queryKey: ['userPoints', userId],
    queryFn: () => PointService.getPointBalance(userId),
    refetchInterval: 30000, // Refetch every 30 seconds
  });

  // Mutation for charging points
  const chargePointsMutation = useMutation({
    mutationFn: (request: PointChargeRequest) => PointService.chargePoints(request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['userPoints', userId] });
      setChargeAmount(0);
      alert('포인트가 성공적으로 충전되었습니다!');
    },
    onError: (error: Error) => {
      alert(`포인트 충전 실패: ${error.message}`);
    },
  });

  const handleChargePoints = () => {
    if (chargeAmount <= 0) {
      alert('충전할 포인트를 입력해주세요.');
      return;
    }

    chargePointsMutation.mutate({
      userId,
      amount: chargeAmount
    });
  };

  const presetAmounts = [1000, 5000, 10000, 50000];

  return (
    <div className="point-manager">
      <div className="current-balance">
        <h3>현재 포인트</h3>
        {isLoading ? (
          <p>로딩 중...</p>
        ) : (
          <p className="balance">{pointBalance?.balance.toLocaleString() || 0}P</p>
        )}
      </div>

      <div className="charge-section">
        <h4>포인트 충전</h4>
        
        <div className="preset-buttons">
          {presetAmounts.map(amount => (
            <button
              key={amount}
              onClick={() => setChargeAmount(amount)}
              className={chargeAmount === amount ? 'active' : ''}
            >
              {amount.toLocaleString()}원
            </button>
          ))}
        </div>

        <div className="custom-amount">
          <input
            type="number"
            value={chargeAmount}
            onChange={(e) => setChargeAmount(parseInt(e.target.value) || 0)}
            placeholder="충전할 금액을 입력하세요"
            min="0"
          />
        </div>

        <button
          onClick={handleChargePoints}
          disabled={chargePointsMutation.isPending || chargeAmount <= 0}
          className="charge-button"
        >
          {chargePointsMutation.isPending ? '충전 중...' : '포인트 충전'}
        </button>
      </div>
    </div>
  );
};
```

---

## Testing Strategies

### 1. API Service Testing

```typescript
// __tests__/api/productService.test.ts
import { ProductService } from '../../api/productService';
import { httpClient } from '../../api/httpClient';

// Mock the httpClient
jest.mock('../../api/httpClient');
const mockedHttpClient = httpClient as jest.Mocked<typeof httpClient>;

describe('ProductService', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('getProductList', () => {
    it('should return products when API call succeeds', async () => {
      const mockProducts = [
        { id: 1, productName: '테스트 상품', amount: 10000, quantity: 10 }
      ];
      
      mockedHttpClient.get.mockResolvedValue({
        success: true,
        data: mockProducts
      });

      const result = await ProductService.getProductList();
      
      expect(result).toEqual(mockProducts);
      expect(mockedHttpClient.get).toHaveBeenCalledWith('/products');
    });

    it('should throw error when API call fails', async () => {
      mockedHttpClient.get.mockResolvedValue({
        success: false,
        error: '서버 오류'
      });

      await expect(ProductService.getProductList()).rejects.toThrow('서버 오류');
    });
  });
});
```

### 2. Component Testing with React Testing Library

```tsx
// __tests__/components/ProductList.test.tsx
import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import ProductList from '../../components/ProductList';
import { ProductService } from '../../api/productService';

// Mock the ProductService
jest.mock('../../api/productService');
const mockedProductService = ProductService as jest.Mocked<typeof ProductService>;

const createWrapper = () => {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: {
        retry: false,
      },
    },
  });

  return ({ children }: { children: React.ReactNode }) => (
    <QueryClientProvider client={queryClient}>
      {children}
    </QueryClientProvider>
  );
};

describe('ProductList', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('renders loading state initially', () => {
    mockedProductService.getProductList.mockImplementation(
      () => new Promise(() => {}) // Never resolves
    );

    render(<ProductList />, { wrapper: createWrapper() });
    
    expect(screen.getByText('상품 목록을 불러오는 중...')).toBeInTheDocument();
  });

  it('renders products when data is loaded', async () => {
    const mockProducts = [
      { id: 1, productName: '테스트 상품 1', amount: 10000, quantity: 5 },
      { id: 2, productName: '테스트 상품 2', amount: 20000, quantity: 3 }
    ];

    mockedProductService.getProductList.mockResolvedValue(mockProducts);

    render(<ProductList />, { wrapper: createWrapper() });

    await waitFor(() => {
      expect(screen.getByText('테스트 상품 1')).toBeInTheDocument();
      expect(screen.getByText('테스트 상품 2')).toBeInTheDocument();
    });
  });

  it('renders error message when API fails', async () => {
    mockedProductService.getProductList.mockRejectedValue(
      new Error('네트워크 오류')
    );

    render(<ProductList />, { wrapper: createWrapper() });

    await waitFor(() => {
      expect(screen.getByText(/네트워크 오류/)).toBeInTheDocument();
    });
  });
});
```

### 3. E2E Testing with Cypress

```typescript
// cypress/e2e/ecommerce.cy.ts
describe('E-commerce Flow', () => {
  beforeEach(() => {
    // Setup test data
    cy.task('db:seed');
    cy.visit('/');
  });

  it('should complete full purchase flow', () => {
    // View products
    cy.get('[data-testid="product-list"]').should('be.visible');
    cy.get('[data-testid="product-card"]').should('have.length.greaterThan', 0);

    // Add product to cart
    cy.get('[data-testid="product-card"]').first().within(() => {
      cy.get('[data-testid="quantity-input"]').clear().type('2');
      cy.get('[data-testid="add-to-cart-btn"]').click();
    });

    // Verify cart
    cy.get('[data-testid="cart-count"]').should('contain', '1');
    
    // Proceed to checkout
    cy.get('[data-testid="checkout-btn"]').click();
    
    // Complete order
    cy.get('[data-testid="confirm-order-btn"]').click();
    
    // Verify success
    cy.get('[data-testid="order-success"]').should('be.visible');
    cy.get('[data-testid="order-id"]').should('be.visible');
  });

  it('should handle out of stock products', () => {
    // Mock out of stock product
    cy.intercept('GET', '/api/products', { fixture: 'products-out-of-stock.json' });
    
    cy.visit('/');
    
    cy.get('[data-testid="product-card"]').first().within(() => {
      cy.get('[data-testid="add-to-cart-btn"]').should('be.disabled');
      cy.contains('품절').should('be.visible');
    });
  });
});
```

---

## Performance Optimization

### 1. Caching Strategies

#### HTTP Client with Cache Headers
```typescript
// api/httpClient.ts - Enhanced with caching
class HttpClient {
  constructor(baseURL: string = 'http://localhost:8080') {
    this.client = axios.create({
      baseURL,
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        'Cache-Control': 'public, max-age=300' // 5 minutes cache
      },
      timeout: 10000
    });
  }

  // Add caching interceptor
  private setupCacheInterceptor() {
    this.client.interceptors.response.use(
      (response) => {
        // Add ETag for cache validation
        const etag = response.headers.etag;
        if (etag) {
          this.client.defaults.headers.common['If-None-Match'] = etag;
        }
        return response;
      }
    );
  }
}
```

#### React Query Optimized Configuration
```typescript
// config/queryClient.ts
import { QueryClient } from '@tanstack/react-query';

export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      // Stale time: data stays fresh for 5 minutes
      staleTime: 5 * 60 * 1000,
      // Cache time: data stays in cache for 30 minutes
      cacheTime: 30 * 60 * 1000,
      // Retry failed requests 3 times
      retry: 3,
      // Don't refetch on window focus by default
      refetchOnWindowFocus: false,
      // Refetch on network reconnection
      refetchOnReconnect: true,
    },
    mutations: {
      // Retry failed mutations once
      retry: 1,
    },
  },
});

// Prefetch critical data
export const prefetchCriticalData = async () => {
  await Promise.all([
    queryClient.prefetchQuery({
      queryKey: ['products'],
      queryFn: ProductService.getProductList,
    }),
    // Add other critical queries
  ]);
};
```

### 2. Code Splitting and Lazy Loading

```tsx
// App.tsx - Route-based code splitting
import React, { Suspense } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { ErrorBoundary } from './components/ErrorBoundary';
import LoadingSpinner from './components/LoadingSpinner';

// Lazy load components
const ProductList = React.lazy(() => import('./components/ProductList'));
const OrderHistory = React.lazy(() => import('./components/OrderHistory'));
const PointManager = React.lazy(() => import('./components/PointManager'));

const App: React.FC = () => {
  return (
    <ErrorBoundary>
      <Router>
        <div className="app">
          <nav>
            {/* Navigation components */}
          </nav>
          
          <main>
            <Suspense fallback={<LoadingSpinner />}>
              <Routes>
                <Route path="/" element={<ProductList />} />
                <Route path="/orders" element={<OrderHistory />} />
                <Route path="/points" element={<PointManager />} />
              </Routes>
            </Suspense>
          </main>
        </div>
      </Router>
    </ErrorBoundary>
  );
};
```

### 3. Virtual Scrolling for Large Lists

```tsx
// components/VirtualizedProductList.tsx
import React from 'react';
import { FixedSizeList as List } from 'react-window';
import { useProducts } from '../hooks/useProducts';

const ProductRow: React.FC<{ index: number; style: any }> = ({ index, style }) => {
  const { data: products } = useProducts();
  const product = products?.[index];

  if (!product) return null;

  return (
    <div style={style} className="product-row">
      <ProductCard product={product} />
    </div>
  );
};

const VirtualizedProductList: React.FC = () => {
  const { data: products, isLoading } = useProducts();

  if (isLoading) return <div>Loading...</div>;
  if (!products) return null;

  return (
    <div className="virtualized-list-container">
      <List
        height={600} // Container height
        itemCount={products.length}
        itemSize={200} // Each item height
        width="100%"
      >
        {ProductRow}
      </List>
    </div>
  );
};
```

### 4. Optimistic Updates

```typescript
// hooks/useOptimisticOrder.ts
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { OrderService, OrderRequest, OrderResponse } from '../api/orderService';

export const useOptimisticOrder = () => {
  const queryClient = useQueryClient();

  return useMutation<OrderResponse, Error, OrderRequest>({
    mutationFn: OrderService.orderAndPay,
    onMutate: async (variables) => {
      // Cancel outgoing refetches
      await queryClient.cancelQueries({ queryKey: ['products'] });

      // Snapshot previous value
      const previousProducts = queryClient.getQueryData(['products']);

      // Optimistically update product quantities
      queryClient.setQueryData(['products'], (oldData: Product[] | undefined) => {
        if (!oldData) return oldData;
        
        return oldData.map(product => 
          product.id === variables.productId
            ? { ...product, quantity: product.quantity - variables.quantity }
            : product
        );
      });

      // Return rollback function
      return { previousProducts };
    },
    onError: (err, variables, context) => {
      // Rollback on error
      if (context?.previousProducts) {
        queryClient.setQueryData(['products'], context.previousProducts);
      }
    },
    onSettled: () => {
      // Always refetch after mutation
      queryClient.invalidateQueries({ queryKey: ['products'] });
    },
  });
};
```

### 5. Bundle Optimization

```typescript
// webpack.config.js additions for optimization
module.exports = {
  // ... other config
  optimization: {
    splitChunks: {
      chunks: 'all',
      cacheGroups: {
        // Vendor chunks
        vendor: {
          test: /[\\/]node_modules[\\/]/,
          name: 'vendors',
          chunks: 'all',
        },
        // API services
        api: {
          test: /[\\/]src[\\/]api[\\/]/,
          name: 'api',
          chunks: 'all',
        },
        // Components
        components: {
          test: /[\\/]src[\\/]components[\\/]/,
          name: 'components',
          chunks: 'all',
        },
      },
    },
  },
  resolve: {
    alias: {
      // Tree shaking for lodash
      'lodash': 'lodash-es',
    },
  },
};
```

---

## Additional Considerations

### 1. Internationalization (i18n)
For Korean/English dual support:

```typescript
// i18n/messages.ts
export const messages = {
  ko: {
    'product.loading': '상품을 불러오는 중...',
    'product.error': '상품을 불러올 수 없습니다',
    'order.success': '주문이 완료되었습니다',
    'point.charge.success': '포인트가 충전되었습니다',
  },
  en: {
    'product.loading': 'Loading products...',
    'product.error': 'Failed to load products',
    'order.success': 'Order completed successfully',
    'point.charge.success': 'Points charged successfully',
  }
};
```

### 2. Progressive Web App (PWA) Features
```typescript
// serviceWorker.ts - Cache API responses
const CACHE_NAME = 'hhplus-ecommerce-v1';
const urlsToCache = [
  '/',
  '/static/js/bundle.js',
  '/static/css/main.css',
];

self.addEventListener('fetch', (event) => {
  // Cache API responses for offline access
  if (event.request.url.includes('/api/products')) {
    event.respondWith(
      caches.match(event.request).then((response) => {
        return response || fetch(event.request);
      })
    );
  }
});
```

### 3. Real-time Features
For coupon availability or stock updates:

```typescript
// hooks/useWebSocket.ts
import { useEffect, useState } from 'react';

export const useWebSocket = (url: string) => {
  const [socket, setSocket] = useState<WebSocket | null>(null);
  const [data, setData] = useState<any>(null);

  useEffect(() => {
    const ws = new WebSocket(url);
    
    ws.onmessage = (event) => {
      const message = JSON.parse(event.data);
      setData(message);
    };

    setSocket(ws);

    return () => {
      ws.close();
    };
  }, [url]);

  return { socket, data };
};

// Usage for real-time stock updates
const ProductList = () => {
  const { data: stockUpdate } = useWebSocket('ws://localhost:8080/stock-updates');
  
  useEffect(() => {
    if (stockUpdate) {
      // Update product stock in real-time
      queryClient.setQueryData(['products'], (oldData: Product[] | undefined) => {
        if (!oldData) return oldData;
        return oldData.map(product => 
          product.id === stockUpdate.productId 
            ? { ...product, quantity: stockUpdate.newQuantity }
            : product
        );
      });
    }
  }, [stockUpdate]);
};
```

---

This comprehensive guide provides everything frontend developers need to integrate with the HangHae Plus E-commerce Spring API. The examples focus on real-world patterns, Korean localization context, and production-ready code quality.

Key files referenced in this guide:
- `/frontend-api-integration-guide.md` - This comprehensive documentation
- Backend API controllers in `/src/main/java/ecommerce/*/adapter/in/web/`
- Common response structure in `/src/main/java/ecommerce/common/dto/ApiResponse.java`
- Application configuration in `/src/main/resources/application.yml`