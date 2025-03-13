<template>
  <div class="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
    <div class="flex h-16 items-center justify-between">
      <div class="flex items-center">
        <div class="shrink-0">
          <img class="h-8 w-auto" src="https://tailwindui.com/plus-assets/img/logos/mark.svg?color=indigo&shade=500"
               alt="Your Company"/>
        </div>
        <div class="hidden sm:ml-6 sm:block">
          <div class="flex space-x-4">
            <!-- Current: "bg-gray-900 text-white", Default: "text-gray-300 hover:bg-gray-700 hover:text-white" -->
            <a @click="goHome" 
               class="rounded-md bg-gray-900 px-3 py-2 text-sm font-medium text-white">Home</a>
            <a @click="goLogin"
               class="rounded-md px-3 py-2 text-sm font-medium text-gray-300 hover:bg-gray-700 hover:text-white">Login</a>
            <a href="#"
               class="rounded-md px-3 py-2 text-sm font-medium text-gray-300 hover:bg-gray-700 hover:text-white">Register</a>
          </div>
        </div>
      </div>
      <div class="hidden sm:ml-6 sm:block">
        <div class="flex items-center">
          <button type="button"
                  class="relative rounded-full bg-gray-800 p-1 text-gray-400 hover:text-white focus:ring-2 focus:ring-white focus:ring-offset-2 focus:ring-offset-gray-800 focus:outline-hidden">
            <span class="absolute -inset-1.5"/>
            <span class="sr-only">View notifications</span>
            <BellIcon class="size-6" aria-hidden="true"/>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {BellIcon} from '@heroicons/vue/24/outline'
import router from "../router.ts";

const goHome = () => {
  router.push('/')
}
const goLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
</style>
