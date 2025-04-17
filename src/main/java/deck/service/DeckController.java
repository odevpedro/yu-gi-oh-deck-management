package deck.service;

import com.odevpedro.yugiohcollections.deck.src.application.dto.DeckDtoMapper;
import com.odevpedro.yugiohcollections.deck.src.application.dto.DeckInputDTO;
import com.odevpedro.yugiohcollections.deck.src.application.dto.DeckOutputDTO;
import com.odevpedro.yugiohcollections.deck.src.application.service.CreateDeckUseCase;
import com.odevpedro.yugiohcollections.deck.src.application.service.DeleteDeckUseCase;
import com.odevpedro.yugiohcollections.deck.src.application.service.FindDeckByIdUseCase;
import com.odevpedro.yugiohcollections.deck.src.application.service.ListDecksByOwnerUseCase;
import com.odevpedro.yugiohcollections.deck.src.domain.model.Deck;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/decks")
public class DeckController {

    private final CreateDeckUseCase createDeckUseCase;
    private final ListDecksByOwnerUseCase listDecksByOwnerUseCase;
    private final FindDeckByIdUseCase findDeckByIdUseCase;
    private final DeleteDeckUseCase deleteDeckUseCase;
    private final DeckDtoMapper deckDtoMapper;

    public DeckController(
            CreateDeckUseCase createDeckUseCase,
            ListDecksByOwnerUseCase listDecksByOwnerUseCase,
            FindDeckByIdUseCase findDeckByIdUseCase,
            DeleteDeckUseCase deleteDeckUseCase,
            DeckDtoMapper deckDtoMapper
    ) {
        this.createDeckUseCase = createDeckUseCase;
        this.listDecksByOwnerUseCase = listDecksByOwnerUseCase;
        this.findDeckByIdUseCase = findDeckByIdUseCase;
        this.deleteDeckUseCase = deleteDeckUseCase;
        this.deckDtoMapper = deckDtoMapper;
    }

    @PostMapping
    public ResponseEntity<DeckOutputDTO> create(@RequestBody DeckInputDTO dto) {
        Deck created = createDeckUseCase.execute(deckDtoMapper.toDomain(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(deckDtoMapper.toOutput(created));
    }

    @GetMapping
    public ResponseEntity<List<DeckOutputDTO>> listByOwner(@RequestParam String ownerId) {
        List<DeckOutputDTO> result = listDecksByOwnerUseCase.execute(ownerId)
                .stream()
                .map(deckDtoMapper::toOutput)
                .toList();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id, @RequestParam String ownerId) {
        return findDeckByIdUseCase.execute(id, ownerId)
                .map(deckDtoMapper::toOutput)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Deck não encontrado"));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @RequestParam String ownerId) {
        deleteDeckUseCase.execute(id, ownerId);
        return ResponseEntity.noContent().build();
    }
}