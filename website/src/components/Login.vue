<script setup lang="ts">

</script>

<template>
  <div class="flex min-h-full flex-1 flex-col justify-center px-6 py-12 lg:px-8">
    <div class="sm:mx-auto sm:w-full sm:max-w-sm">
      <img class="mx-auto h-10 w-auto"
           src="https://tailwindui.com/plus-assets/img/logos/mark.svg?color=indigo&shade=500" alt="Your Company"/>
      <h2 class="mt-10 text-center text-xl/9 font-bold tracking-tight text-white">welcome to xyz</h2>
    </div>

    <div class="mt-10 sm:mx-auto sm:w-full sm:max-w-sm">
      <form class="space-y-6" action="#" method="POST">
        <div>
          <label for="email" class="block text-sm/6 font-medium text-white">username</label>
          <div class="mt-2">
            <input type="email" name="email" id="email" autocomplete="email" required=""
                   class="block w-full rounded-md bg-white/5 px-3 py-1.5 text-base text-white outline-1 
                   -outline-offset-1 outline-white/10 placeholder:text-gray-500 focus:outline-2 
                   focus:-outline-offset-2 focus:outline-gray-400 sm:text-sm/6"/>
          </div>
        </div>

        <div>
          <div class="flex items-center justify-between">
            <label for="password" class="block text-sm/6 font-medium text-white">password</label>
          </div>
          <div class="mt-2">
            <input type="password" name="password" id="password" autocomplete="current-password" required=""
                   class="block w-full rounded-md bg-white/5 px-3 py-1.5 text-base text-white outline-1 
                   -outline-offset-1 outline-white/10 placeholder:text-gray-500 focus:outline-2 f
                   ocus:-outline-offset-2 focus:outline-gray-400 sm:text-sm/6"/>
          </div>
        </div>

        <div>
          <button type="submit"
                  class="flex w-full justify-center rounded-md bg-neutral-800 px-3 py-1.5 text-sm/6 font-semibold 
                  text-white shadow-xs hover:bg-indigo-400 focus-visible:outline-2 focus-visible:outline-offset-2 
                  focus-visible:outline-indigo-500">
            Sign in
          </button>
        </div>
      </form>

      <p class="mt-10 text-center text-sm/6 text-gray-300">
        Not a member?
        {{ ' ' }}
        <a href="#" class="font-semibold text-gray-200 hover:text-indigo-300 underline">Register here</a>
      </p>
    </div>
  </div>
</template>


<style scoped>

</style>