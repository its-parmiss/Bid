package rahnema.tumaj.bid.backend.utils.exceptions.NotFoundExceptions;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(Long id){
        super("Category with id: " + id + " doesn't exist");
    }
}
