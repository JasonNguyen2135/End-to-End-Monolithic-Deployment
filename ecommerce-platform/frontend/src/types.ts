export interface Product {
    id?: string;
    name: string;
    description: string;
    price: number;
    imageUrl: string;
    stock: number; // Bổ sung trường stock
    categoryName?: string;
    categoryId?: number;
}

export interface OrderRequest {
    orderLineItemsDtoList: OrderLineItem[];
}

export interface OrderLineItem {
    skuCode: string;
    price: number;
    quantity: number;
}
