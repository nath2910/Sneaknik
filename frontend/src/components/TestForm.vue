<template>
  <form @submit.prevent="addTechno">
    <!-- eviter le comportement de base avec .prevent-->
    <label for="techno"> techno a ajouter</label>
    <br />
    <input type="text" name="techno" v-model="techTolearn" />
    <br />
    <button type="submit">ajouter</button>
  </form>
</template>

<script lang="ts">
import { ref } from 'vue'

export default {
  setup(props, ctx) {
    const techTolearn = ref('') //grace a ref ça deviens reactif

    const addTechno = function () {
      console.log('Form | addtechno()', techTolearn.value)
      ctx.emit('add', techTolearn.value) // permet de transmettre a app.vue
      techTolearn.value = '' //donner l'impression que ça marche bien en enlevenant la valeur
    }
    return {
      techTolearn: techTolearn,
      addTechno,
    }
  },
}
</script>

<style></style>
