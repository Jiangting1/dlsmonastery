package net.myspring.basic.modules.hr.web.controller;

import net.myspring.basic.modules.hr.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "hr/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

}
