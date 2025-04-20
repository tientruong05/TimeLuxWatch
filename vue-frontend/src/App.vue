<script setup>
// Keep the script setup if you need it for App-level logic in the future
import HeaDer from "@/components/HeaDer.vue"; // Import Header
import FooTer from "@/components/FooTer.vue"; // Import Footer
</script>

<template>
  <HeaDer />
  <!-- Add Header component -->
  <main>
    <router-view />
    <!-- Router view renders the current route component -->
  </main>
  <FooTer />
  <!-- Add Footer component -->
</template>

<style></style>
