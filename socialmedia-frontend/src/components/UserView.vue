<template>
    <v-container class="user-container">
      <h2 class="text-h5">User Profile</h2>
      <v-card outlined class="user-card mb-5">
        <v-card-title>
          <span>{{ user.name || "Unknown User" }}</span>
        </v-card-title>
        <v-card-text>
          <p>Email: {{ user.email || "Not Available" }}</p>
          <p>Joined on: {{ user.createdOn || "Unknown" }}</p>
        </v-card-text>
        <v-btn color="primary" outlined small @click="$emit('go-back')">Back to Posts</v-btn>
      </v-card>
  
      <!-- User's Posts Section -->
      <h2 class="text-h5 mt-5">My Posts</h2>
      <v-row>
        <v-col cols="12" md="6" v-for="post in userPosts" :key="post.id">
          <v-card outlined class="mb-5 post-card">
            <!-- Post Header -->
            <v-card-title>{{ post.title }}</v-card-title>
            <v-card-text>{{ post.content }}</v-card-text>
            <v-card-subtitle>Created on: {{ post.createdOn }}</v-card-subtitle>
  
            <!-- Comments Section -->
            <v-divider class="mt-3"></v-divider>
            <h4 class="text-h6 mt-3 comments-title">Comments</h4>
            <v-list dense>
              <v-list-item v-for="comment in post.comments" :key="comment.id">
                <v-list-item-content>
                  <v-list-item-title>
                    <strong>{{ comment.user?.name || "Unknown User" }}:</strong> {{ comment.content }}
                  </v-list-item-title>
                  <v-list-item-subtitle>
                    {{ comment.createdOn }}
                  </v-list-item-subtitle>
                </v-list-item-content>
              </v-list-item>
            </v-list>
          </v-card>
        </v-col>
      </v-row>
    </v-container>
  </template>
  
  <script>
  import axios from "axios";
  
  export default {
    name: "UserView",
    props: {
      user: {
        type: Object,
        required: true,
      },
    },
    data() {
      return {
        userPosts: [], 
      };
    },
    methods: {
      async fetchUserPosts() {
        try {
          const response = await axios.get(`http://localhost:8082/posts/user/${this.user.id}`);
          this.userPosts = response.data.map(post => ({
            ...post,
            comments: post.comments.map(comment => ({
              ...comment,
              user: { name: comment.userName || "Unknown User" },
            })),
          }));
        } catch (error) {
          console.error("Error fetching user posts:", error);
        }
      },
    },
    mounted() {
      this.fetchUserPosts();
    },
  };
  </script>
  
  <style scoped>
  .user-container {
    margin-top: 20px;
  }
  
  .user-card {
    background-color: #4a148c;
    color: white;
    border-radius: 10px;
    padding: 20px;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  }
  
  .post-card {
    background-color: #c9d2ea;
    color: black;
    border-radius: 10px;
    padding: 20px;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  }
  
  .comments-title {
    font-weight: bold;
    color: #1b5e20;
  }
  
  h2 {
    color: #4a148c;
  }
  
  .v-divider {
    margin-top: 20px;
    margin-bottom: 10px;
  }
  </style>
  