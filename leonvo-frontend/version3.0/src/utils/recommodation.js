// 商品推荐算法工具类

// 计算商品相似度
function calculateSimilarity(product1, product2) {
    let score = 0;

    // 1. 品牌相似度（40%权重）
    if (product1.brand && product2.brand) {
        if (product1.brand === product2.brand) {
            score += 40;
        }
    }

    // 2. 价格相似度（30%权重）
    if (product1.price && product2.price) {
        const priceDiff = Math.abs(product1.price - product2.price);
        const priceRatio = priceDiff / Math.max(product1.price, product2.price);
        if (priceRatio <= 0.2) { // 价格差异在20%以内
            score += 30;
        } else if (priceRatio <= 0.5) { // 价格差异在50%以内
            score += 15;
        }
    }

    // 3. 名称关键词相似度（30%权重）
    if (product1.name && product2.name) {
        const name1 = product1.name.toLowerCase();
        const name2 = product2.name.toLowerCase();

        // 检查共同关键词
        const keywords = ['联想', 'ThinkPad', 'Yoga', 'Legion', 'IdeaPad', 'ThinkBook', '笔记本', '电脑'];
        const commonKeywords = keywords.filter(keyword =>
            name1.includes(keyword) && name2.includes(keyword)
        );

        if (commonKeywords.length > 0) {
            score += 30;
        } else {
            // 简单分词匹配
            const words1 = name1.split(/[\s\-_]+/);
            const words2 = name2.split(/[\s\-_]+/);
            const commonWords = words1.filter(word =>
                word.length > 1 && words2.includes(word)
            );
            if (commonWords.length > 0) {
                score += 15;
            }
        }
    }

    return score;
}

// 生成推荐商品
export function generateRecommendations(cartItems, allProducts, options = {}) {
    const {
        maxRecommendations = 4,
        minSimilarityScore = 50
    } = options;

    if (cartItems.length === 0 || allProducts.length === 0) {
        return [];
    }

    const recommendations = new Map();

    // 对购物车中的每个商品，找到最相似的几个商品
    cartItems.forEach(cartItem => {
        const similarProducts = allProducts
            .filter(product => product.id !== cartItem.id) // 排除自己
            .map(product => ({
                product,
                similarity: calculateSimilarity(cartItem, product)
            }))
            .filter(item => item.similarity >= minSimilarityScore) // 过滤掉相似度太低的
            .sort((a, b) => b.similarity - a.similarity) // 按相似度降序
            .slice(0, 2) // 每个商品最多推荐2个
            .map(item => item.product);

        // 添加到推荐列表
        similarProducts.forEach(product => {
            if (!recommendations.has(product.id)) {
                recommendations.set(product.id, {
                    ...product,
                    recommendCount: 1,
                    recommendReasons: [`与"${cartItem.name}"相似`]
                });
            } else {
                const existing = recommendations.get(product.id);
                existing.recommendCount += 1;
                existing.recommendReasons.push(`与"${cartItem.name}"相似`);
            }
        });
    });

    // 转换为数组并按推荐次数排序
    const recommendationsArray = Array.from(recommendations.values())
        .sort((a, b) => {
            // 首先按推荐次数排序
            if (b.recommendCount !== a.recommendCount) {
                return b.recommendCount - a.recommendCount;
            }
            // 其次按价格排序（高价优先）
            return b.price - a.price;
        })
        .slice(0, maxRecommendations);

    return recommendationsArray;
}

// 获取热门商品（当购物车为空时显示）
export function getPopularProducts(allProducts, count = 4) {
    return [...allProducts]
        .sort((a, b) => {
            // 可以在这里添加更多排序逻辑，如销量、评分等
            return b.price - a.price; // 暂时按价格排序
        })
        .slice(0, count);
}