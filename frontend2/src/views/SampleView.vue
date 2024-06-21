<script setup lang="ts">
import {ref, watchEffect} from 'vue'

interface Product {
  id: number;
  name: string;
  description: string;
  price: number;
}

const API_URL = `/api/products`
const product = ref<Product[]>([]);
const name = ref("")
const description = ref("")
const price = ref("")


watchEffect(async () => {
  const url = `${API_URL}`
  product.value = await (await fetch(url)).json()
});

const insert = async () => {

  // POST request using fetch with async/await
  const requestOptions = {
    method: "POST",
    headers: {"Content-Type": "application/json"},
    body: JSON.stringify({name: name.value, description: description.value, price: price.value})
  };
  const response = await fetch('/api/products', requestOptions);
  const data = await response.json();
  // product.value = data
  product.value.push(data)
}
</script>

<template>
  <h1>Sample Component</h1>
  <div>
    name: <input type="text" v-model="name">
    &nbsp;description: <input type="text" v-model="description">
    &nbsp;price: <input type="text" v-model="price">
    &nbsp;<button @click="insert">추가</button>
  </div>
  <div>
    <ul v-if="product.length">
      <li v-for="item of product">{{ item }}</li>
    </ul>
  </div>
</template>