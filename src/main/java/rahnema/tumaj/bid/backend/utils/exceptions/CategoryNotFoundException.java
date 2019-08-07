package rahnema.tumaj.bid.backend.utils.exceptions;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(Long id){
        super("Category with id: " + id + " doesn't exist");
    }
}
