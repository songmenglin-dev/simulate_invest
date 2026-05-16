<script setup lang="ts">
defineProps<{ title: string; data: Record<string, string | number> }>()
</script>

<template>
  <div class="bg-white rounded-xl border border-gray-100 p-5">
    <h3 class="text-sm font-semibold text-gray-700 mb-3">{{ title }}</h3>
    <table class="w-full">
      <tbody>
        <tr v-for="(value, key) in data" :key="key" class="border-b border-gray-50 last:border-0">
          <td class="py-2 text-sm text-gray-500">{{ key }}</td>
          <td class="py-2 text-sm text-right font-medium text-gray-900">{{ typeof value === 'number' ? value.toLocaleString() : value }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
