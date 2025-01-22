<template>
  <v-app>
    <v-main>
      <div v-if="loggedIn">
        <!-- App Bar -->
        <div
          style="
            display: flex;
            justify-content: space-between;
            align-items: center;
            background-color: #340039;
            color: white;
            padding: 10px 20px;
          "
        >
          <h2>Best app ever</h2>
          <div>
            <v-btn color="light-blue" outlined small @click="goToUserPage">User</v-btn>
            <v-btn color="red" small @click="logout">Logout</v-btn>
          </div>
        </div>

        <!-- Content Section -->
        <div style="max-width: 1000px; margin: auto; margin-top: 20px;">
          <posts-view v-if="currentPage === 'posts'" />
          <user-view
            v-else-if="currentPage === 'user'"
            :user="currentUser"
            @go-back="goToPostsPage"
          />
        </div>
      </div>

      <div v-else>
        <!-- Login/Sign-Up Page -->
        <auth-page @auth-success="handleAuthSuccess"></auth-page>
      </div>
    </v-main>
  </v-app>
</template>

<script>
import PostsView from "@/components/PostsView.vue";
import AuthPage from "@/components/AuthPage.vue";
import UserView from "@/components/UserView.vue"; 

export default {
  name: "App",
  components: {
    PostsView,
    AuthPage,
    UserView,
  },
  data() {
    return {
      loggedIn: false,
      username: "",
      currentPage: "posts", 
      currentUser: null, 
    };
  },
  methods: {
    handleAuthSuccess(user) {
      this.currentUser = user;
      this.username = user.name;
      this.loggedIn = true;
      localStorage.setItem("user", JSON.stringify(user)); 
    },
    logout() {
      this.loggedIn = false;
      this.username = "";
      this.currentUser = null;
      this.currentPage = "posts";
      localStorage.removeItem("user");
    },
    goToUserPage() {
      this.currentPage = "user"; 
    },
    goToPostsPage() {
      this.currentPage = "posts"; 
    },
  },
  mounted() {
    const user = JSON.parse(localStorage.getItem("user"));
    if (user) {
      this.currentUser = user;
      this.username = user.name;
      this.loggedIn = true;
    }
  },
};
</script>
