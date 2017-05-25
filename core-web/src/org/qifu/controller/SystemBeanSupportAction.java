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
package org.qifu.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.qifu.base.controller.BaseController;
import org.qifu.base.exception.AuthorityException;
import org.qifu.base.exception.ControllerException;
import org.qifu.base.exception.ServiceException;
import org.qifu.base.model.ControllerMethodAuthority;
import org.qifu.base.model.DefaultControllerJsonResultObj;
import org.qifu.base.model.DefaultResult;
import org.qifu.base.model.PageOf;
import org.qifu.base.model.QueryControllerJsonResultObj;
import org.qifu.base.model.QueryResult;
import org.qifu.base.model.SearchValue;
import org.qifu.base.model.YesNo;
import org.qifu.po.TbSys;
import org.qifu.po.TbSysBeanHelp;
import org.qifu.service.ISysBeanHelpService;
import org.qifu.service.ISysService;
import org.qifu.service.logic.ISystemBeanHelpLogicService;
import org.qifu.vo.SysBeanHelpVO;
import org.qifu.vo.SysVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@Controller
public class SystemBeanSupportAction extends BaseController {
	
	private ISysBeanHelpService<SysBeanHelpVO, TbSysBeanHelp, String> sysBeanHelpService;
	private ISysService<SysVO, TbSys, String> sysService;
	private ISystemBeanHelpLogicService systemBeanHelpLogicService;
	
	public ISysBeanHelpService<SysBeanHelpVO, TbSysBeanHelp, String> getSysBeanHelpService() {
		return sysBeanHelpService;
	}
	
	@Autowired
	@Resource(name="core.service.SysBeanHelpService")
	@Required
	public void setSysBeanHelpService(ISysBeanHelpService<SysBeanHelpVO, TbSysBeanHelp, String> sysBeanHelpService) {
		this.sysBeanHelpService = sysBeanHelpService;
	}
	
	public ISysService<SysVO, TbSys, String> getSysService() {
		return sysService;
	}
	
	@Autowired
	@Resource(name="core.service.SysService")
	@Required
	public void setSysService(ISysService<SysVO, TbSys, String> sysService) {
		this.sysService = sysService;
	}
	
	public ISystemBeanHelpLogicService getSystemBeanHelpLogicService() {
		return systemBeanHelpLogicService;
	}
	
	@Autowired
	@Resource(name="core.service.logic.SystemBeanHelpLogicService")
	@Required
	public void setSystemBeanHelpLogicService(ISystemBeanHelpLogicService systemBeanHelpLogicService) {
		this.systemBeanHelpLogicService = systemBeanHelpLogicService;
	}
	
	private void init(String type, HttpServletRequest request, ModelAndView mv) throws ServiceException, ControllerException, Exception {
		if ("queryPage".equals(type) || "createPage".equals(type) || "editPage".equals(type)) {
			mv.addObject( "sysMap", sysService.findSysMap(this.getBasePath(request), true) );
		}
	}
	
	private void fetchData(SysBeanHelpVO sysBeanHelp, ModelAndView mv) throws ServiceException, ControllerException, Exception {
		DefaultResult<SysBeanHelpVO> result = this.sysBeanHelpService.findObjectByOid(sysBeanHelp);
		if ( result.getValue() == null ) {
			throw new ControllerException( result.getSystemMessage().getValue() );
		}
		sysBeanHelp = result.getValue();
		mv.addObject("sysBeanHelp", sysBeanHelp);
		
		TbSys sys = new TbSys();
		sys.setSysId( sysBeanHelp.getSystem() );
		DefaultResult<TbSys> sysResult = this.sysService.findEntityByUK(sys);
		if (sysResult.getValue() == null) {
			throw new ControllerException( sysResult.getSystemMessage().getValue() );
		}
		sys = sysResult.getValue();
		mv.addObject("systemOid", sys.getOid());
	}
	
	@ControllerMethodAuthority(check = true, programId = "CORE_PROG003D0003Q")
	@RequestMapping(value = "/core.sysBeanSupportManagement.do")	
	public ModelAndView queryPage(HttpServletRequest request) {
		String viewName = PAGE_SYS_ERROR;
		ModelAndView mv = this.getDefaultModelAndView("CORE_PROG003D0003Q");
		try {
			this.init("queryPage", request, mv);
			viewName = "sys-beansupport/sys-beansupport-management";
		} catch (AuthorityException e) {
			viewName = PAGE_SYS_NO_AUTH;
		} catch (ServiceException | ControllerException e) {
			viewName = PAGE_SYS_SEARCH_NO_DATA;
		} catch (Exception e) {
			e.printStackTrace();
			this.setPageMessage(request, e.getMessage().toString());
		}
		mv.setViewName(viewName);
		return mv;
	}	
	
	@ControllerMethodAuthority(check = true, programId = "CORE_PROG003D0003Q")
	@RequestMapping(value = "/core.sysBeanSupportQueryGridJson.do", produces = "application/json")	
	public @ResponseBody QueryControllerJsonResultObj< List<SysBeanHelpVO> > queryGrid(SearchValue searchValue, PageOf pageOf) {
		QueryControllerJsonResultObj< List<SysBeanHelpVO> > result = this.getQueryJsonResult("CORE_PROG003D0003Q");
		if (!this.isAuthorizeAndLoginFromControllerJsonResult(result)) {
			return result;
		}
		try {
			QueryResult< List<SysBeanHelpVO> > queryResult = this.sysBeanHelpService.findGridResult(searchValue, pageOf);
			this.setQueryGridJsonResult(result, queryResult, pageOf);
		} catch (AuthorityException | ServiceException | ControllerException e) {
			result.setMessage( e.getMessage().toString() );
		} catch (Exception e) {
			e.printStackTrace();
			result.setMessage( e.getMessage().toString() );
		}
		return result;
	}
	
	@ControllerMethodAuthority(check = true, programId = "CORE_PROG003D0003A")
	@RequestMapping(value = "/core.sysBeanSupportCreate.do")
	public ModelAndView createPage(HttpServletRequest request) {
		String viewName = PAGE_SYS_ERROR;
		ModelAndView mv = this.getDefaultModelAndView("CORE_PROG003D0003A");
		try {
			this.init("createPage", request, mv);
			viewName = "sys-beansupport/sys-beansupport-create";
		} catch (AuthorityException e) {
			viewName = PAGE_SYS_NO_AUTH;
		} catch (ServiceException | ControllerException e) {
			viewName = PAGE_SYS_SEARCH_NO_DATA;
		} catch (Exception e) {
			e.printStackTrace();
			this.setPageMessage(request, e.getMessage().toString());
		}
		mv.setViewName(viewName);
		return mv;
	}	
	
	@ControllerMethodAuthority(check = true, programId = "CORE_PROG003D0003E")
	@RequestMapping(value = "/core.sysBeanSupportEdit.do")
	public ModelAndView editPage(HttpServletRequest request, SysBeanHelpVO sysBeanHelp) {
		String viewName = PAGE_SYS_ERROR;
		ModelAndView mv = this.getDefaultModelAndView("CORE_PROG003D0003E");
		try {
			this.init("editPage", request, mv);
			this.fetchData(sysBeanHelp, mv);
			viewName = "sys-beansupport/sys-beansupport-edit";
		} catch (AuthorityException e) {
			viewName = PAGE_SYS_NO_AUTH;
		} catch (ServiceException | ControllerException e) {
			viewName = PAGE_SYS_SEARCH_NO_DATA;
		} catch (Exception e) {
			e.printStackTrace();
			this.setPageMessage(request, e.getMessage().toString());
		}
		mv.setViewName(viewName);
		return mv;
	}		
	
	private void checkFields(DefaultControllerJsonResultObj<SysBeanHelpVO> result, SysBeanHelpVO sysBeanHelp, String systemOid) throws ControllerException, Exception {
		this.getCheckControllerFieldHandler(result)
		.testField("systemOid", ( this.noSelect(systemOid) ), "Please select system!")
		.testField("beanId", sysBeanHelp, "@org.apache.commons.lang3.StringUtils@isBlank( beanId )", "Bean Id is blank!")
		.testField("beanId", sysBeanHelp, "!@org.qifu.util.SimpleUtils@checkBeTrueOf_azAZ09( beanId.replaceAll(\"[.]\", \"\") )", "Bean Id only normal character!")
		.testField("method", sysBeanHelp, "@org.apache.commons.lang3.StringUtils@isBlank( method )", "Method is blank!")
		.testField("method", sysBeanHelp, "!@org.qifu.util.SimpleUtils@checkBeTrueOf_azAZ09( method.replaceAll(\"_\", \"\") )", "Method only normal character!")
		.throwMessage();
	}
	
	private void save(DefaultControllerJsonResultObj<SysBeanHelpVO> result, SysBeanHelpVO sysBeanHelp, String systemOid) throws AuthorityException, ControllerException, ServiceException, Exception {
		this.checkFields(result, sysBeanHelp, systemOid);
		DefaultResult<SysBeanHelpVO> cResult = this.systemBeanHelpLogicService.create(sysBeanHelp, systemOid);
		if ( cResult.getValue() != null ) {
			result.setValue( cResult.getValue() );
			result.setSuccess( YesNo.YES );
		}
		result.setMessage( cResult.getSystemMessage().getValue() );		
	}	
	
	private void update(DefaultControllerJsonResultObj<SysBeanHelpVO> result, SysBeanHelpVO sysBeanHelp, String systemOid) throws AuthorityException, ControllerException, ServiceException, Exception {
		this.checkFields(result, sysBeanHelp, systemOid);
		DefaultResult<SysBeanHelpVO> uResult = this.systemBeanHelpLogicService.update(sysBeanHelp, systemOid);
		if ( uResult.getValue() != null ) {
			result.setValue( uResult.getValue() );
			result.setSuccess( YesNo.YES );
		}
		result.setMessage( uResult.getSystemMessage().getValue() );		
	}	
	
	private void delete(DefaultControllerJsonResultObj<Boolean> result, SysBeanHelpVO sysBeanHelp) throws AuthorityException, ControllerException, ServiceException, Exception {
		DefaultResult<Boolean> dResult = this.systemBeanHelpLogicService.delete(sysBeanHelp);
		if ( dResult.getValue() != null && dResult.getValue() ) {
			result.setValue( dResult.getValue() );
			result.setSuccess( YesNo.YES );
		}
		result.setMessage( dResult.getSystemMessage().getValue() );		
	}	 
	
	@ControllerMethodAuthority(check = true, programId = "CORE_PROG003D0003A")
	@RequestMapping(value = "/core.sysBeanSupportSaveJson.do", produces = "application/json")		
	public @ResponseBody DefaultControllerJsonResultObj<SysBeanHelpVO> doSave(SysBeanHelpVO sysBeanHelp, @RequestParam("systemOid") String systemOid) {
		DefaultControllerJsonResultObj<SysBeanHelpVO> result = this.getDefaultJsonResult("CORE_PROG003D0003A");
		if (!this.isAuthorizeAndLoginFromControllerJsonResult(result)) {
			return result;
		}
		try {
			this.save(result, sysBeanHelp, systemOid);
		} catch (AuthorityException | ServiceException | ControllerException e) {
			result.setMessage( e.getMessage().toString() );			
		} catch (Exception e) {
			e.printStackTrace();
			result.setMessage( e.getMessage().toString() );
		}
		return result;
	}	
	
	@ControllerMethodAuthority(check = true, programId = "CORE_PROG003D0003E")
	@RequestMapping(value = "/core.sysBeanSupportUpdateJson.do", produces = "application/json")		
	public @ResponseBody DefaultControllerJsonResultObj<SysBeanHelpVO> doUpdate(SysBeanHelpVO sysBeanHelp, @RequestParam("systemOid") String systemOid) {
		DefaultControllerJsonResultObj<SysBeanHelpVO> result = this.getDefaultJsonResult("CORE_PROG003D0003E");
		if (!this.isAuthorizeAndLoginFromControllerJsonResult(result)) {
			return result;
		}
		try {
			this.update(result, sysBeanHelp, systemOid);
		} catch (AuthorityException | ServiceException | ControllerException e) {
			result.setMessage( e.getMessage().toString() );			
		} catch (Exception e) {
			e.printStackTrace();
			result.setMessage( e.getMessage().toString() );
		}
		return result;
	}
	
	@ControllerMethodAuthority(check = true, programId = "CORE_PROG003D0003D")
	@RequestMapping(value = "/core.sysBeanSupportDeleteJson.do", produces = "application/json")		
	public @ResponseBody DefaultControllerJsonResultObj<Boolean> doDelete(SysBeanHelpVO sysBeanHelp) {
		DefaultControllerJsonResultObj<Boolean> result = this.getDefaultJsonResult("CORE_PROG003D0003D");
		if (!this.isAuthorizeAndLoginFromControllerJsonResult(result)) {
			return result;
		}
		try {
			this.delete(result, sysBeanHelp);
		} catch (AuthorityException | ServiceException | ControllerException e) {
			result.setMessage( e.getMessage().toString() );			
		} catch (Exception e) {
			e.printStackTrace();
			result.setMessage( e.getMessage().toString() );
		}
		return result;
	}	
	
}