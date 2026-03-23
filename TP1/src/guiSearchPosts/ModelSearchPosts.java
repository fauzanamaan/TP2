package guiSearchPosts;
import entityClasses.Post;
import guiRole1.ModelRole1Home;
import java.util.List;                                                       
/**                                                                          
* <p> Title: ModelSearchPosts Class </p>
*                                                                           
* <p> Description: Model for Search Posts functionality - handles search
logic </p>                                                                   
*             
* <p> Copyright: Lynn Robert Carter © 2025 </p>
*                                                                           
* @version 1.00 2026-03-22 Initial implementation for Read & Search         
functionality                                                                
*/
public class ModelSearchPosts {                                              
	/**
    * Searches for posts by keyword, optionally filtered by thread
    */                                                                      
    public static List<Post> search(String keyword, String thread) {
         return ModelRole1Home.searchPosts(keyword, thread);                  
    }          
    /**
    * Gets the read status of a post for the current user
    */                                                                      
    public static boolean isPostRead(int postId) {
    	return ModelRole1Home.isRead(postId);                                
    }          
}                                                                            

