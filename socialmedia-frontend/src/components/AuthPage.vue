<template>
  <v-container>
    <v-card class="pa-4" outlined>
      <h3 class="text-h5 mb-4">Login or Register</h3>
      <v-tabs v-model="tab">
        <v-tab>Login</v-tab>
        <v-tab>Sign Up</v-tab>
      </v-tabs>

      <v-tabs-items v-model="tab">
        <!-- Login Tab -->
        <v-tab-item>
          <v-form @submit.prevent="login">
            <v-text-field
              label="Email"
              v-model="loginData.email"
              outlined
              required
            ></v-text-field>
            <v-btn color="primary" type="submit">Login</v-btn>
          </v-form>
        </v-tab-item>

        <!-- Sign-Up Tab -->
        <v-tab-item>
          <v-form @submit.prevent="signUp">
            <v-text-field
              label="Email"
              v-model="signUpData.email"
              outlined
              required
            ></v-text-field>
            <v-text-field
              label="Name"
              v-model="signUpData.name"
              outlined
              required
            ></v-text-field>
            <v-btn color="primary" type="submit">Sign Up</v-btn>
          </v-form>
        </v-tab-item>
      </v-tabs-items>
    </v-card>
  </v-container>
</template>

<script>
import axios from "axios";

export default {
  name: "AuthPage",
  data() {
    return {
      tab: 0, 
      loginData: {
        email: "",
      },
      signUpData: {
        email: "",
        name: "",
      },
    };
  },
  methods: {
    async login() {
      try {
        const response = await axios.get(`http://localhost:8082/user/email/${this.loginData.email}`);
        if (response.data) {
          this.$emit("auth-success", response.data);
        } else {
          alert("User not found. Please register first.");
        }
      } catch (error) {
        console.error("Error during login:", error);
      }
    },
    async signUp() {
      try {
        const response = await axios.post("http://localhost:8082/user/register", this.signUpData);
        this.$emit("auth-success", response.data);
      } catch (error) {
        console.error("Error during sign-up:", error);
      }
    },
  },
};
</script>
