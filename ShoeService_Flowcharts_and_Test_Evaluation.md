# ShoeService Control Flow Diagrams and Unit Test Evaluation

## Control Flow Diagrams (Mermaid Code)

### 1. getAllShoesForAdmin()
```mermaid
graph TD
    A[Start getAllShoesForAdmin] --> B[Call shoeRepository.findAll()]
    B --> C[Stream shoes to map shoeHelper.getShoeResponse]
    C --> D[Collect to List<ShoeResponse>]
    D --> E[Return List<ShoeResponse>]
    E --> F[End]
```

### 2. getAllShoes()
```mermaid
graph TD
    A[Start getAllShoes] --> B[Call shoeRepository.findByStatusTrue()]
    B --> C[Stream shoes to map shoeHelper.getShoeResponse]
    C --> D[Collect to List<ShoeResponse>]
    D --> E[Return List<ShoeResponse>]
    E --> F[End]
```

### 3. getShoeById(int id)
```mermaid
graph TD
    A[Start getShoeById(id)] --> B[Call shoeRepository.findById(id)]
    B --> C{Optional.isPresent()?}
    C -->|Yes| D[Get shoe from Optional]
    D --> E[Call shoeHelper.getShoeResponse(shoe)]
    E --> F[Return ShoeResponse]
    F --> G[End]
    C -->|No| H[Throw AppException(ErrorCode.PRODUCT_NOT_FOUND)]
    H --> G
```

### 4. getShoesByGender(String gender)
```mermaid
graph TD
    A[Start getShoesByGender(gender)] --> B[Convert gender to upper case]
    B --> C[Get ShoeConstants.Gender.valueOf(gender)]
    C --> D[Call shoeRepository.findByStatusTrueAndGender(genderEnum)]
    D --> E[Stream shoes to map shoeHelper.getShoeResponse]
    E --> F[Collect to List<ShoeResponse>]
    F --> G[Return List<ShoeResponse>]
    G --> H[End]
```

### 5. getShoesByBrand(Integer brandId)
```mermaid
graph TD
    A[Start getShoesByBrand(brandId)] --> B[Call brandRepository.findById(brandId)]
    B --> C{Optional.isPresent()?}
    C -->|Yes| D[Get brand from Optional]
    D --> E[Call shoeRepository.findByStatusTrueAndBrand(brand)]
    E --> F[Stream shoes to map shoeHelper.getShoeResponse]
    F --> G[Collect to List<ShoeResponse>]
    G --> H[Return List<ShoeResponse>]
    H --> I[End]
    C -->|No| J[Throw AppException(ErrorCode.BRAND_NOT_FOUND)]
    J --> I
```

### 6. getShoesByCategory(String categoryName)
```mermaid
graph TD
    A[Start getShoesByCategory(categoryName)] --> B[Call ShoeConstants.getCategoryFromString(categoryName)]
    B --> C{CategoryEnum == null?}
    C -->|Yes| D[Throw AppException(ErrorCode.PRODUCT_NOT_FOUND)]
    D --> E[End]
    C -->|No| F[Call shoeRepository.findByStatusTrueAndCategory(categoryEnum)]
    F --> G[Stream shoes to map shoeHelper.getShoeResponse]
    G --> H[Collect to List<ShoeResponse>]
    H --> I[Return List<ShoeResponse>]
    I --> E
```

### 7. createShoe(ShoeCreateRequest request)
```mermaid
graph TD
    A[Start createShoe(request)] --> B{request.getImages() == null or empty?}
    B -->|Yes| C[Throw AppException(ErrorCode.INVALID_KEY)]
    C --> D[End]
    B -->|No| E[Create new Shoe()]
    E --> F[Set name, price, description, status, createdAt]
    F --> G[Get genderEnum = ShoeConstants.getGenderFromString(request.getGender())]
    G --> H[Get categoryEnum = ShoeConstants.getCategoryFromString(request.getCategory())]
    H --> I{genderEnum == null or categoryEnum == null?}
    I -->|Yes| J[Throw AppException(ErrorCode.INVALID_KEY)]
    J --> D
    I -->|No| K[Set gender, category]
    K --> L[Set fakePrice]
    L --> M[Call brandRepository.findById(request.getBrandId())]
    M --> N{Optional.isPresent()?}
    N -->|Yes| O[Get brand, set to shoe]
    O --> P[Call shoeRepository.save(newShoe)]
    P --> Q[Map images: stream request.getImages() to ShoeImage via imageMapper]
    Q --> R[Set shoe to each image, set createdAt]
    R --> S[Call shoeImageRepository.saveAll(images)]
    S --> T{request.getVariants() != null and not empty?}
    T -->|Yes| U[Stream variants to ShoeVariant via shoeVariantMapper]
    U --> V[For each variant: get sizeId, find sizeChart, set size, generate SKU, check unique, set shoe, sku]
    V --> W[Call shoeVariantRepository.saveAll(variants)]
    W --> X[Call shoeHelper.getShoeResponse(newShoe)]
    X --> Y[Return ShoeResponse]
    Y --> D
    T -->|No| X
    N -->|No| Z[Throw AppException(ErrorCode.BRAND_NOT_FOUND)]
    Z --> D
```

### 8. getShoesByName(String name)
```mermaid
graph TD
    A[Start getShoesByName(name)] --> B[Call shoeRepository.findByStatusTrueAndNameContainingIgnoreCase(name)]
    B --> C[Stream shoes to map shoeHelper.getShoeResponse]
    C --> D[Collect to List<ShoeResponse>]
    D --> E[Return List<ShoeResponse>]
    E --> F[End]
```

### 9. updateShoe(ShoeUpdateRequest request, int shoeId)
```mermaid
graph TD
    A[Start updateShoe(request, shoeId)] --> B[Call shoeRepository.findById(shoeId)]
    B --> C{Optional.isPresent()?}
    C -->|Yes| D[Get selectedShoe]
    D --> E[Set updatedAt, name, price, description, fakePrice, gender, category, status]
    E --> F{request.getImages() != null and not empty?}
    F -->|Yes| G[Get oldImages = new ArrayList<>(selectedShoe.getShoeImages())]
    G --> H[For each oldImage: shoeImageRepository.delete(oldImage)]
    H --> I[selectedShoe.getShoeImages().clear()]
    I --> J[Map new images: stream to ShoeImage, set publicId via extractPublicIdFromUrl, set url, shoe, createdAt, updatedAt]
    J --> K[selectedShoe.getShoeImages().addAll(newImages)]
    K --> L[For each variantRequest in request.getVariants()]
    L --> M[Find existingVariant by variantId]
    M --> N{existingVariant == null?}
    N -->|Yes| O[Throw AppException(ErrorCode.INVALID_KEY)]
    O --> P[End]
    N -->|No| Q[Set updatedAt, stockQuantity]
    Q --> R[Set selectedShoe.setShoeVariants(updatedVariants)]
    R --> S[Call shoeRepository.save(selectedShoe)]
    S --> T[Call shoeHelper.getShoeResponse(selectedShoe)]
    T --> U[Return ShoeResponse]
    U --> P
    F -->|No| L
    C -->|No| V[Throw AppException(ErrorCode.PRODUCT_NOT_FOUND)]
    V --> P
```

### 10. getShoePaging(...)
```mermaid
graph TD
    A[Start getShoePaging(...)] --> B[Call createSort(sortOrder)]
    B --> C[Create Pageable with page-1, size, sort]
    C --> D[Get genderEnum = ShoeConstants.getGenderFromString(genderString)]
    D --> E[Get categoryEnum = ShoeConstants.getCategoryFromString(categoryString)]
    E --> F[Call shoeRepository.findShoesByFilters(name, minPrice, maxPrice, brandId, genderEnum, categoryEnum, status, pageable)]
    F --> G[Get shoeData = result.getContent()]
    G --> H[Stream shoeData to map shoeHelper.getShoeResponse]
    H --> I[Collect to shoeList]
    I --> J[Build PageResponse with currentPage, pageSize, totalPages, totalElements, data]
    J --> K[Return PageResponse<ShoeResponse>]
    K --> L[End]
```

### 11. shoeData(String messageContent)
```mermaid
graph TD
    A[Start shoeData(messageContent)] --> B[Call this.getAllShoes()]
    B --> C[Call objectMapper.writeValueAsString(result)]
    C --> D[Call MessageUtil.createMessages("You are a helpful assistant " + messageContent, jsonArray)]
    D --> E[Create ChatRequest("gpt-4o-mini", messages)]
    E --> F[Call chatClient.generate(chatRequest)]
    F --> G[Get response = chatResponse.getChoices().getFirst().getMessage().getContent()]
    G --> H[Return response]
    H --> I[End]
```

### 12. deleteShoe(int id)
```mermaid
graph TD
    A[Start deleteShoe(id)] --> B[Call shoeRepository.findById(id)]
    B --> C{Optional.isPresent()?}
    C -->|Yes| D[Get shoe]
    D --> E[Set shoe.setStatus(false)]
    E --> F[Set shoe.setUpdatedAt(Instant.now())]
    F --> G[Call shoeRepository.save(shoe)]
    G --> H[Call shoeVariantRepository.findShoeVariantByShoeId(id)]
    H --> I[For each variant: set updatedAt]
    I --> J[Call shoeVariantRepository.saveAll(variants)]
    J --> K[End]
    C -->|No| L[Throw AppException(ErrorCode.PRODUCT_NOT_FOUND)]
    L --> K
```

### 13. createSort(String sortOrder) [Private]
```mermaid
graph TD
    A[Start createSort(sortOrder)] --> B{sortOrder == null?}
    B -->|Yes| C[Return Sort.by(ASC, "createdAt")]
    C --> D[End]
    B -->|No| E[Switch sortOrder.toLowerCase()]
    E -->|"desc"| F[Return Sort.by(DESC, "price")]
    E -->|"asc"| G[Return Sort.by(ASC, "price")]
    E -->|"date_desc"| H[Return Sort.by(DESC, "createdAt")]
    E -->|default| I[Return Sort.by(ASC, "createdAt")]
    F --> D
    G --> D
    H --> D
    I --> D
```

### 14. extractPublicIdFromUrl(String url) [Private]
```mermaid
graph TD
    A[Start extractPublicIdFromUrl(url)] --> B{url == null or empty?}
    B -->|Yes| C[Return UUID.randomUUID().toString()]
    C --> D[End]
    B -->|No| E{url.startsWith("/uploads/")?}
    E -->|Yes| F[Split by "/", return parts[parts.length-1]]
    F --> D
    E -->|No| G[Try: split by "/", get filename, split by "?" remove query]
    G --> H[Return filenameWithExt.split("\\?")[0]]
    H --> D
    G -->|Exception| I[Return UUID.randomUUID().toString()]
    I --> D
```

## Evaluation of ShoeServiceUnitTest.java

The unit tests in `ShoeServiceUnitTest.java` are **not fully appropriate** for the following reasons:

### Coverage Issues:
- **Incomplete Method Coverage**: Out of 12 public methods in ShoeService, only 5 are tested:
  - `getAllShoes` (1 test)
  - `getShoeById` (2 tests: exists and not exists)
  - `createShoe` (1 test)
  - `deleteShoe` (1 test)
- **Missing Tests**: No tests for `getAllShoesForAdmin`, `getShoesByGender`, `getShoesByBrand`, `getShoesByCategory`, `getShoesByName`, `updateShoe`, `getShoePaging`, `shoeData`.
- **Private Methods**: `createSort` and `extractPublicIdFromUrl` are not tested, though they could be tested indirectly or via integration tests.

### Depth of Testing:
- **Basic Scenarios Only**: Tests cover happy paths but miss error cases and edge conditions.
  - `createShoe` test doesn't check: no images (should throw), invalid gender/category (should throw), brand not found, variants with invalid size, SKU already exists, etc.
  - `deleteShoe` test assumes no variants; doesn't test with variants.
  - No tests for paging parameters, sorting, AI chat integration, etc.
- **Mocking**: Relies heavily on mocks, which is good for unit tests, but doesn't verify complex interactions or real repository behavior.
- **Assertions**: Limited; e.g., `createShoe` only verifies saves, not the returned response or variant creation.

### Recommendations:
- Add tests for all public methods.
- Include edge cases: null inputs, invalid enums, not found entities, empty lists.
- Test complex logic like variant creation in `createShoe`, image updates in `updateShoe`.
- Consider parameterized tests for enums and filters.
- Add tests for private methods if they have complex logic (e.g., `extractPublicIdFromUrl`).
- Increase assertion depth to verify returned data, not just method calls.

Overall, the tests provide a basic foundation but lack comprehensiveness for robust unit testing.