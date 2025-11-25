package com.projectlyrics.server.domain.common.dto.util;

import java.util.List;

public record IdsWithHasNext(List<Long> ids, boolean hasNext) {}

