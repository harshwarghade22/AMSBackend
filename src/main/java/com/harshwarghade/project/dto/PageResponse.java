package com.harshwarghade.project.dto;

import lombok.Data;
import java.util.List;

@Data
public class PageResponse<T> implements java.io.Serializable {

    private List<T> content;
    private boolean last;
    private int number;
    private int totalPages;
    private int size;
}
