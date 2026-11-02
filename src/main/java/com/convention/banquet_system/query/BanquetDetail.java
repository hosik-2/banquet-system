package com.convention.banquet_system.query;

import com.convention.banquet_system.domain.Banquet;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BanquetDetail {

    private Banquet banquet;
    private AuditInfo auditInfo;

}
