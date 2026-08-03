package com.kh.myweb.common.template;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.kh.myweb.common.model.vo.PageInfo;

class PaginationTest {

    @Test
    void getPageInfo_shouldCalculatePaginationForSinglePage() {
        PageInfo pageInfo = Pagination.getPageInfo(5, 1, 10, 5);

        assertEquals(5, pageInfo.getListCount());
        assertEquals(1, pageInfo.getCurrentPage());
        assertEquals(10, pageInfo.getPageLimit());
        assertEquals(5, pageInfo.getBoardLimit());
        assertEquals(1, pageInfo.getMaxPage());
        assertEquals(1, pageInfo.getStartPage());
        assertEquals(1, pageInfo.getEndPage());
    }

    @Test
    void getPageInfo_shouldClampEndPageToMaxPage() {
        PageInfo pageInfo = Pagination.getPageInfo(25, 11, 10, 5);

        assertEquals(5, pageInfo.getMaxPage());
        assertEquals(11, pageInfo.getStartPage());
        assertEquals(5, pageInfo.getEndPage());
    }
}
