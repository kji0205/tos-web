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

  if (!(name.value && description.value && price.value)) {
    alert('입력값 확인')
    return;
  }

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
  <div>
    <div class="pa-6">
      <v-text-field label="name" v-model="name"/>
      <v-text-field label="description" v-model="description"/>
      <v-text-field label="price" v-model="price"/>
      <v-btn @click="insert">
        상품 추가
      </v-btn>
    </div>

    <v-divider></v-divider>

    <div class="pa-6">
      <v-table>
        <thead>
        <tr>
          <th class="text-left">
            Name
          </th>
          <th class="text-left">
            description
          </th>
          <th class="text-left">
            price
          </th>
        </tr>
        </thead>
        <tbody>
        <tr
            v-for="item of product"
            :key="item.id"
        >
          <td>{{ item.name }}</td>
          <td>{{ item.description }}</td>
          <td>{{ item.price }}</td>
        </tr>
        </tbody>
      </v-table>
    </div>
  </div>
</template>