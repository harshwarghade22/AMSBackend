package com.harshwarghade.project.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PageResponse<T> implements java.io.Serializable {

    private List<T> content;
    private boolean last;
    private int number;
    private int totalPages;
    private int size;
}
