// 购物车工具类

// 购物车本地存储的key
const CART_KEY = 'lenovo_cart';

// 获取购物车所有商品
function getCart() {
    const cartData = localStorage.getItem(CART_KEY);
    if (cartData) {
        return JSON.parse(cartData);
    }
    return [];
}

// 添加商品到购物车
function addToCart(product) {
    const cart = getCart();

    // 查找是否已存在
    let found = false;
    for (let item of cart) {
        if (item.id === product.id) {
            item.quantity += 1;
            found = true;
            break;
        }
    }

    // 如果不存在，添加新商品
    if (!found) {
        cart.push({
            id: product.id,
            name: product.name,
            price: product.price,
            imageUrl: product.imageUrl,
            quantity: 1
        });
    }

    // 保存到本地存储
    localStorage.setItem(CART_KEY, JSON.stringify(cart));

    return cart;
}

// 获取购物车商品数量
function getCartCount() {
    const cart = getCart();
    let count = 0;
    for (let item of cart) {
        count += item.quantity;
    }
    return count;
}

// 导出函数
export { getCart, addToCart, getCartCount };