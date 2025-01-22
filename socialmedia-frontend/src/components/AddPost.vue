
<script>
import axios from 'axios';

export default {

  name: 'posts',
  props: {
    currentUser: String
  },
  data() {
    return {
      showDialog: Boolean,
      post: {
        title: "",
        content: "",
      }
    }
  },
  methods: {
    async createPost() {
  const { title, content } = this.newPost;
  if (!title || !content) return;

  try {
    // Retrieve the logged-in user
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

  }
};
</script>

<style scoped>
.dialogClass {
  padding: 20px;
  background-color: white;
}
</style>