/*
 * ============================================================================
 * = COPYRIGHT
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION
 *   This software is supplied under the terms of a license agreement or
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied
 *   or disclosed except in accordance with the terms in that agreement.
 *   	Copyright (C) 2009-2020 PAX Technology, Inc. All rights reserved.
 * ============================================================================
 */
package com.pax.common.service.impl;

import java.util.List;

import org.apache.commons.codec.binary.StringUtils;

import com.pax.common.dao.IBaseDao;
import com.pax.common.pagination.IPageForm;
import com.pax.common.pagination.Page;
import com.pax.common.service.IBaseService;

public abstract class BaseService<M extends java.io.Serializable, PK extends java.io.Serializable>
        implements IBaseService <M, PK> {

    public abstract IBaseDao <M, PK> getBaseDao();

    @Override
    public M save(M model) {
        getBaseDao().save(model);
        return model;
    }

    @Override
    public void update(M model) {
        getBaseDao().update(model);
    }

    @Override
    public void delete(PK id) {
        getBaseDao().delete(id);
    }

    @Override
    public void deleteObject(M model) {
        getBaseDao().deleteObject(model);
    }

    @Override
    public M get(PK id) {
        return getBaseDao().get(id);
    }

    @Override
    public long count() {
        return getBaseDao().count();
    }

    @Override
    public List <M> list() {
        return getBaseDao().list();
    }

    @Override
    public List <M> list(int start, int length) {
        return getBaseDao().list(start, length);
    }

    @Override
    public <S extends java.io.Serializable> long count(S command) {
        return getBaseDao().count(command);
    }

    @Override
    public <E, S extends IPageForm> Page <E> page(S command) {
        long totalCount = getBaseDao().count(command);
        int index = command.getPageIndex();
        int size = command.getPageSize();
        int firstResult = Page.getPageStart(totalCount, index, size);
        return Page.getPage(index, size, totalCount, getBaseDao().page(command, firstResult, size));
    }

    @Override
    public <E, S extends IPageForm> List <E> list(S command) {
        return getBaseDao().list(command);
    }

    protected boolean isModified(String oldStr, String newStr) {
        return !StringUtils.equals(oldStr, newStr);
    }

}
