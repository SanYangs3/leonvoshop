<!-- src/views/HomeView.vue -->
<template>
  <div class="home">
    <ProductList />
  </div>
</template>

<script>
import ProductList from '@/components/HelloWorld.vue';

export default {
  name: 'HomeView',
  components: {
    ProductList
  }
};
</script>

<style>
.home {
  padding: 0;
  margin: 0;
}
</style>