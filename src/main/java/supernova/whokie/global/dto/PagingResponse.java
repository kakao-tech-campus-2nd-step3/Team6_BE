package supernova.whokie.global.dto;

import lombok.Builder;
import org.springframework.data.domain.Page;

import java.util.List;

@Builder
public record PagingResponse<T>(
        List<T> content,
        long totalElements,
        int totalPages,
        int size,
        int page
) {
    public static <T> PagingResponse<T> from(Page<T> page) {
        return new PagingResponse<>(
                page.getContent(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getSize(),
                page.getNumber()
        );
    }
}