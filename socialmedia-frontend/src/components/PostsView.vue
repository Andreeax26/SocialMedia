<template>
  <v-container fluid class="main-container">

    <!-- Content Section -->
    <v-container class="content-container">
      <!-- Create a New Post Section -->
      <v-card outlined class="mb-5 new-post-card">
        <h2 class="text-h5 mb-3">Create a New Post</h2>
        <v-form @submit.prevent="createPost">
          <v-text-field
            label="Post Title"
            v-model="newPost.title"
            outlined
            dense
            required
          ></v-text-field>
          <v-textarea
            label="Post Content"
            v-model="newPost.content"
            outlined
            dense
            rows="4"
            required
          ></v-textarea>
          <v-btn color="primary" dark type="submit" class="mt-3">Add Post</v-btn>
        </v-form>
      </v-card>

      <!-- Posts Section -->
      <h2 class="text-h5 mb-3">My Posts</h2>
      <v-btn color="primary" outlined dark @click="fetchPosts" class="mb-5">Load Posts</v-btn>
      <v-row>
        <v-col cols="12" md="6" v-for="post in sortedPosts" :key="post.id">
          <v-card outlined class="mb-5 post-card">
            <!-- Post Header -->
            <v-card-title class="d-flex justify-space-between align-center">
              <span class="post-title">{{ post.title }}</span>
              <div>
                <v-btn icon small color="info" @click="editPost(post)">
                  <v-icon small>mdi-pencil</v-icon>
                </v-btn>
                <v-btn icon small color="error" @click="deletePost(post.id)">
                  <v-icon small>mdi-delete</v-icon>
                </v-btn>
              </div>
            </v-card-title>
            <!-- Post Subtitle -->
            <v-card-subtitle>
              Posted by <strong>{{ post.user?.name || "Unknown User" }}</strong> on {{ post.createdOn }} {{ post.status}}
            </v-card-subtitle>
            <!-- Post Content -->
            <v-card-text>
              {{ post.content }}
            </v-card-text>

            <!-- Comments Section -->
            <v-divider class="mt-3"></v-divider>
            <h4 class="text-h6 mt-3">Comments</h4>
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
                <v-btn icon small color="info" @click="editComment(post.id, comment)">
                  <v-icon small>mdi-pencil</v-icon>
                </v-btn>
                <v-btn icon small color="error" @click="deleteComment(post.id, comment.id)">
                  <v-icon small>mdi-delete</v-icon>
                </v-btn>
              </v-list-item>
            </v-list>

            <!-- Add Comment Section -->
            <v-textarea
              v-model="newComment[post.id]"
              label="Add a comment"
              outlined
              dense
              rows="2"
              class="mt-3"
            ></v-textarea>
            <v-btn color="success" dark small @click="addComment(post.id)" class="mt-3">Add Comment</v-btn>
          </v-card>
        </v-col>
      </v-row>

      <!-- Edit Post Dialog -->
      <v-dialog v-model="showEditDialog" max-width="500px">
        <v-card>
          <v-card-title>Edit Post</v-card-title>
          <v-card-text>
            <v-form @submit.prevent="updatePost">
              <v-text-field
                label="Post Title"
                v-model="editPostData.title"
                outlined
                dense
                required
              ></v-text-field>
              <v-textarea
                label="Post Content"
                v-model="editPostData.content"
                outlined
                dense
                rows="4"
                required
              ></v-textarea>
            </v-form>
          </v-card-text>
          <v-card-actions>
            <v-btn color="primary" dark @click="updatePost">Save</v-btn>
            <v-btn text @click="closeEditDialog">Cancel</v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
    </v-container>
  </v-container>
</template>

<script>
import axios from "axios";

export default {
  name: "PostsView",
  data() {
    return {
      posts: [], 
      newPost: {
        title: "",
        content: "",
      }, 
      newComment: {}, 
      showEditDialog: false, 
      editPostData: {}, 
    };
  },
  computed: {
    sortedPosts() {
      return this.posts.slice().sort((a, b) => new Date(b.createdOn) - new Date(a.createdOn));
    },
  },
  methods: {
    async fetchPosts() {
    try {
        const response = await axios.get("http://localhost:8082/posts");
        this.posts = response.data.map(post => ({
            ...post,
            user: { name: post.userName || "Unknown User" },
            comments: post.comments.map(comment => ({
                ...comment,
                user: { name: comment.userName || "Unknown User" }
            
            }))
        }));
    } catch (error) {
        console.error("Error fetching posts:", error);
    }
},
    async createPost() {
      const { title, content } = this.newPost;
      if (!title || !content) return;

      try {
        const loggedInUser = JSON.parse(localStorage.getItem("user"));
        const response = await axios.post("http://localhost:8082/posts", {
          title,
          content,
          userId: loggedInUser.id, 
        });
        this.posts.unshift({
          ...response.data,
          user: { name: loggedInUser.name },
        });
        this.newPost.title = "";
        this.newPost.content = "";
      } catch (error) {
        console.error("Error creating post:", error);
      }
    },
    async deletePost(postId) {
      try {
        await axios.delete(`http://localhost:8082/posts/${postId}`);
        this.posts = this.posts.filter(post => post.id !== postId);
      } catch (error) {
        console.error("Error deleting post:", error);
      }
    },
    editPost(post) {
      this.editPostData = { ...post };
      this.showEditDialog = true;
    },
    async updatePost() {
      try {
        const response = await axios.put(`http://localhost:8082/posts/${this.editPostData.id}`, {
          title: this.editPostData.title,
          content: this.editPostData.content,
        });
        const index = this.posts.findIndex(post => post.id === this.editPostData.id);
        if (index !== -1) this.posts.splice(index, 1, response.data);
        this.closeEditDialog();
      } catch (error) {
        console.error("Error updating post:", error);
      }
    },
    closeEditDialog() {
      this.showEditDialog = false;
      this.editPostData = {};
    },
    async addComment(postId) {
    const commentContent = this.newComment[postId];
    if (!commentContent) return;

    try {
        const loggedInUser = JSON.parse(localStorage.getItem("user"));
        const response = await axios.post("http://localhost:8082/comments", {
            content: commentContent,
            userId: loggedInUser.id,
            postId: postId
        });

        const post = this.posts.find(post => post.id === postId);
        if (post) {
            post.comments.push({
                ...response.data,
                user: { name: loggedInUser.name }
            });
        }
        this.newComment[postId] = "";
    } catch (error) {
        console.error("Error adding comment:", error);
    }
},
    async deleteComment(postId, commentId) {
      try {
        await axios.delete(`http://localhost:8082/comments/${commentId}`);
        const post = this.posts.find(post => post.id === postId);
        if (post) {
          post.comments = post.comments.filter(comment => comment.id !== commentId);
        }
      } catch (error) {
        console.error("Error deleting comment:", error);
      }
    },
    async editComment(postId, comment) {
      const updatedContent = prompt("Edit your comment:", comment.content);
      if (!updatedContent) return;

      try {
        const response = await axios.put(`http://localhost:8082/comments/${comment.id}`, {
          content: updatedContent,
        });
        const post = this.posts.find(post => post.id === postId);
        if (post) {
          const commentIndex = post.comments.findIndex(c => c.id === comment.id);
          if (commentIndex !== -1) {
            post.comments.splice(commentIndex, 1, response.data);
          }
        }
      } catch (error) {
        console.error("Error editing comment:", error);
      }
    },
  },
  mounted() {
    this.fetchPosts();
  },
};
</script>

<style scoped>
.main-container {
  background-color: #ecfdfc;
  min-height: 100vh;
  padding: 0;
}

.content-container {
  margin-top: 70px;
}

.new-post-card {
  background-color: #340039;
  border-radius: 10px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.post-card {
  background-color: #c9d2ea;
  border-radius: 10px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  padding: 15px;
}

.post-title {
  font-size: 1.2rem;
  font-weight: bold;
  color: #6188c2;
}

.logout-btn {
  text-transform: uppercase;
  font-weight: bold;
}

.v-btn {
  text-transform: none;
}

.v-card {
  border-radius: 12px;
}

.v-divider {
  margin: 10px 0;
}

.text-h5 {
  font-weight: bold;
  color: #dedbdb;
}

.text-h6 {
  font-weight: bold;
  color: #1b5e20;
}

.mb-5 {
  margin-bottom: 30px;
}

.mt-3 {
  margin-top: 20px;
}

.new-post-card {
  background-color: #4a148c; 
  color: white; 
}

.new-post-card .v-text-field__control,
.new-post-card .v-textarea__control {
  color: white; 
  border-color: white !important; 
}

.new-post-card .v-label {
  color: rgba(255, 255, 255, 0.7);
}

.new-post-card .v-input__control {
  background: transparent !important; 
}

.new-post-card .v-input:before {
  border-bottom: 1px solid rgba(255, 255, 255, 0.5) !important; 
}

.new-post-card .v-input:after {
  border-bottom: 2px solid white !important; 
}
.v-app-bar {
  border-bottom: 4px solid #a95ea8; 
}
</style>

