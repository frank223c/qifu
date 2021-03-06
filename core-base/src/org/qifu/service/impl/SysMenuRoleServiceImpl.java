/* 
 * Copyright 2012-2017 qifu of copyright Chen Xin Nien
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * -----------------------------------------------------------------------
 * 
 * author: 	Chen Xin Nien
 * contact: chen.xin.nien@gmail.com
 * 
 */
package org.qifu.service.impl;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.qifu.base.dao.IBaseDAO;
import org.qifu.base.service.BaseService;
import org.qifu.dao.ISysMenuRoleDAO;
import org.qifu.po.TbSysMenuRole;
import org.qifu.service.ISysMenuRoleService;
import org.qifu.vo.SysMenuRoleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("core.service.SysMenuRoleService")
@Transactional(propagation=Propagation.REQUIRED, readOnly=true)
public class SysMenuRoleServiceImpl extends BaseService<SysMenuRoleVO, TbSysMenuRole, String> implements ISysMenuRoleService<SysMenuRoleVO, TbSysMenuRole, String> {
	protected Logger logger=Logger.getLogger(SysMenuRoleServiceImpl.class);
	private ISysMenuRoleDAO<TbSysMenuRole, String> sysMenuRoleDAO;
	
	public SysMenuRoleServiceImpl() {
		super();
	}

	public ISysMenuRoleDAO<TbSysMenuRole, String> getSysMenuRoleDAO() {
		return sysMenuRoleDAO;
	}

	@Autowired
	@Resource(name="core.dao.SysMenuRoleDAO")
	@Required		
	public void setSysMenuRoleDAO(ISysMenuRoleDAO<TbSysMenuRole, String> sysMenuRoleDAO) {
		this.sysMenuRoleDAO = sysMenuRoleDAO;
	}

	@Override
	protected IBaseDAO<TbSysMenuRole, String> getBaseDataAccessObject() {
		return sysMenuRoleDAO;
	}

	@Override
	public String getMapperIdPo2Vo() {		
		return MAPPER_ID_PO2VO;
	}

	@Override
	public String getMapperIdVo2Po() {
		return MAPPER_ID_VO2PO;
	}

}
