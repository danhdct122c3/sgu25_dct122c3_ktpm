package fpl.sd.backend.mapper;

import fpl.sd.backend.constant.ShoeConstants;
import fpl.sd.backend.dto.request.DiscountCreateRequest;
import fpl.sd.backend.dto.request.DiscountUpdateRequest;
import fpl.sd.backend.dto.response.DiscountResponse;
import fpl.sd.backend.entity.Discount;
import fpl.sd.backend.entity.DiscountCategory;
import fpl.sd.backend.entity.DiscountShoe;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface DiscountMapper {

    Discount toDiscount(DiscountCreateRequest request);

    @Mapping(source = "discountCategories", target = "categories", qualifiedByName = "mapCategories")
    @Mapping(source = "discountShoes", target = "shoeIds", qualifiedByName = "mapShoeIds")
    DiscountResponse toDiscountResponse(Discount discount);

    @Mapping(source = "active", target = "isActive")
    Discount toDiscount(DiscountUpdateRequest request);

    @Named("mapCategories")
    default List<ShoeConstants.Category> mapCategories(List<DiscountCategory> discountCategories) {
        if (discountCategories == null || discountCategories.isEmpty()) {
            return null;
        }
        return discountCategories.stream()
                .map(DiscountCategory::getCategory)
                .collect(Collectors.toList());
    }

    @Named("mapShoeIds")
    default List<String> mapShoeIds(List<DiscountShoe> discountShoes) {
        if (discountShoes == null || discountShoes.isEmpty()) {
            return null;
        }
        return discountShoes.stream()
                .map(ds -> String.valueOf(ds.getShoe().getId()))
                .collect(Collectors.toList());
    }

    @Mapping(source = "startDate", target = "startDate", qualifiedByName = "instantToString")
    @Mapping(source = "endDate", target = "endDate", qualifiedByName = "instantToString")
    @Named("instantToString")
    default String mapInstantToString(Instant instant) {
        if (instant == null) return null;
        return DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
                .withZone(ZoneId.systemDefault())
                .format(instant);
    }
}
