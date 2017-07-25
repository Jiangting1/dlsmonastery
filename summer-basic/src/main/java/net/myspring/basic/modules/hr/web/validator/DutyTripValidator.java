package net.myspring.basic.modules.hr.web.validator;

import net.myspring.basic.modules.hr.web.form.DutyTripForm;
import net.myspring.common.utils.ValidationUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Created by admin on 2016/12/20.
 */
@Component
public class DutyTripValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz){
        return false;
    }

    @Override
    public void validate(Object target, Errors errors){
        DutyTripForm dutyTrip = (DutyTripForm) target;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "dateStart", "error.dateStart", "必填信息");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "dateEnd", "error.dateEnd", "必填信息");
        if(dutyTrip.getDateStart()!=null&&dutyTrip.getDateEnd()!=null){
            if(dutyTrip.getDateEnd().isBefore(dutyTrip.getDateStart())){
                errors.rejectValue("dateStart","error.dateStart","开始日期必须小于等于结束日期");
                errors.rejectValue("dateEnd","error.dateEnd","开始日期必须大于等于结束日期");
            }
            if(ChronoUnit.DAYS.between(dutyTrip.getDateStart(), LocalDateTime.now())>30){
                errors.rejectValue("dateStart","error.dateStart","只能申请10天内数据");
            }
        }
    }
}
