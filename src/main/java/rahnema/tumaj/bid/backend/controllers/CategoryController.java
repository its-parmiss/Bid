package rahnema.tumaj.bid.backend.controllers;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.*;
import rahnema.tumaj.bid.backend.domains.Category.CategoryInputDTO;
import rahnema.tumaj.bid.backend.domains.Category.CategoryOutputDTO;
import rahnema.tumaj.bid.backend.domains.auction.AuctionOutputDTO;
import rahnema.tumaj.bid.backend.models.Auction;
import rahnema.tumaj.bid.backend.models.Category;
import rahnema.tumaj.bid.backend.services.category.CategoryService;
import rahnema.tumaj.bid.backend.utils.assemblers.AuctionAssemler;
import rahnema.tumaj.bid.backend.utils.assemblers.CategoryAssembler;

import java.util.ArrayList;
import java.util.List;

import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class CategoryController {
    private final CategoryService categoryService;
    private final CategoryAssembler assembler;
    private final AuctionAssemler auctionAssemler;


    public CategoryController(CategoryService categoryService, CategoryAssembler assembler, AuctionAssemler auctionAssemler) {
        this.categoryService = categoryService;
        this.assembler = assembler;
        this.auctionAssemler = auctionAssemler;
    }

    @GetMapping(path = "/categories")
    public Resources<Resource<CategoryOutputDTO>> getAllCategories() {
        List<Resource<CategoryOutputDTO>> categories = categoryService.getAll().stream()
                .map(assembler::toResource)
                .collect(Collectors.toList());
        return new Resources<>(
                categories,
                linkTo(methodOn(CategoryController.class).getAllCategories()).withSelfRel()
        );
    }

    @PostMapping(path = "/categories")
    public Resource<CategoryOutputDTO> addCategory(@RequestParam String password, @RequestBody CategoryInputDTO categoryInputDTO) {
        if (password.equals("12345678")) {
            this.categoryService.addOne(categoryInputDTO);
            return assembler.toResource(CategoryInputDTO.toModel(categoryInputDTO));
        } else {
            return null;
        }

    }

    @GetMapping("/auctions/filter")
    public Resources<Resource<AuctionOutputDTO>> filter(@RequestParam Long id) {
        Category category = categoryService.findById(id).get();
        List<Auction> auctions = new ArrayList<>(category.getAuctions());
        System.out.println("auctions.size = " + auctions.size());
        for (Auction a : auctions) {
            System.out.println("a.getTitle() = " + a.getTitle());
        }
        List<Resource<AuctionOutputDTO>> auctionlists = auctions.stream()
                .map(this.auctionAssemler::assemble)
                .collect(Collectors.toList());
        return new Resources<>(auctionlists, linkTo(methodOn(CategoryController.class).filter(id)).withSelfRel());

    }
}