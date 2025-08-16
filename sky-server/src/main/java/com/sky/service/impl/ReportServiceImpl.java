package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    OrderDetailMapper orderDetailMapper;
    @Autowired
    WorkspaceService workspaceService;
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        List<Double> turnoverList = new ArrayList<>();
        for(LocalDate day=begin; day.isBefore(end)||day.isEqual(end);day = day.plusDays(1)){
            dateList.add(day);
            LocalDateTime b = LocalDateTime.of(day, LocalTime.MIN);
            LocalDateTime e = LocalDateTime.of(day,LocalTime.MAX);
            Map map = new HashMap();
            map.put("begin",b);
            map.put("end",e);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.sumByMap(map);
            turnover = turnover == null ? 0.0: turnover;
            turnoverList.add(turnover);
            System.out.println(day);

        }

        String dateL = StringUtils.join(dateList, ",");
        String turnoverL = StringUtils.join(turnoverList,",");
        return TurnoverReportVO.builder()
                .dateList(dateL)
                .turnoverList(turnoverL)
                .build();
    }

    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        List<Integer> totalUserList = new ArrayList<>();
        List<Integer> newUserList = new ArrayList<>();
        Map m = new HashMap();
        m.put("end",LocalDateTime.of(begin.minusDays(1),LocalTime.MAX));
        Integer total = userMapper.countByMap(m);
        for(LocalDate day=begin; day.isBefore(end)||day.isEqual(end);day = day.plusDays(1)){
            dateList.add(day);
            LocalDateTime b = LocalDateTime.of(day, LocalTime.MIN);
            LocalDateTime e = LocalDateTime.of(day,LocalTime.MAX);
            Map map = new HashMap();
            map.put("begin",b);
            map.put("end",e);
            Integer newUser= userMapper.countByMap(map);
            total += newUser;

            totalUserList.add(total);
            newUserList.add(newUser);

            System.out.println(day);

        }

        String dateL = StringUtils.join(dateList, ",");
        String totalUserL = StringUtils.join(totalUserList,",");
        String newUserL = StringUtils.join(newUserList,",");

        return UserReportVO.builder()
                .dateList(dateL)
                .totalUserList(totalUserL)
                .newUserList(newUserL)
                .build();

    }

    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();
        Integer totalOrderCount = 0;
        Integer validOrderCount = 0;
        for(LocalDate day=begin; day.isBefore(end)||day.isEqual(end);day = day.plusDays(1)){
            dateList.add(day);
            LocalDateTime b = LocalDateTime.of(day, LocalTime.MIN);
            LocalDateTime e = LocalDateTime.of(day,LocalTime.MAX);
            Map map = new HashMap();
            map.put("begin",b);
            map.put("end",e);
            Integer orderCount= orderMapper.countByMap(map);
            totalOrderCount+=orderCount;
            map.put("status",Orders.COMPLETED);
            Integer validOrder = orderMapper.countByMap(map);
            validOrderCount+=validOrder;

            orderCountList.add(orderCount);
            validOrderCountList.add(validOrder);

            System.out.println(day);
        }
        Double orderCompletionRate = ((double)validOrderCount)/totalOrderCount;
        String dateL = StringUtils.join(dateList, ",");
        String orderCountL = StringUtils.join(orderCountList,",");
        String validOrderCountL = StringUtils.join(validOrderCountList,",");

        return OrderReportVO.builder()
                .validOrderCountList(validOrderCountL)
                .validOrderCount(validOrderCount)
                .dateList(dateL)
                .orderCompletionRate(orderCompletionRate)
                .orderCountList(orderCountL)
                .totalOrderCount(totalOrderCount)
                .build();
    }

    @Override
    public SalesTop10ReportVO getSalesTop10Statistics(LocalDate begin, LocalDate end) {
        List<String> nameList = new ArrayList<>();
        List<Integer> numberList = new ArrayList<>();
        Map map = new HashMap<>();
        map.put("begin",LocalDateTime.of(begin,LocalTime.MIN));
        map.put("end",LocalDateTime.of(end,LocalTime.MAX));
        map.put("status",Orders.COMPLETED);
        List<GoodsSalesDTO> list = orderDetailMapper.getSalesTop(map);
        for(GoodsSalesDTO goodsSalesDTO:list){
            nameList.add(goodsSalesDTO.getName());
            numberList.add(goodsSalesDTO.getNumber());
        }

        String nameL = StringUtils.join(nameList,",");
        String numberL = StringUtils.join(numberList,",");
        return SalesTop10ReportVO.builder()
                .nameList(nameL)
                .numberList(numberL)
                .build();

    }

    @Override
    public void excelBusinessData(HttpServletResponse response) {
        LocalDate begin = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now().minusDays(1);
        BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(begin, LocalTime.MIN), LocalDateTime.of(end, LocalTime.MAX));
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");

        try {
            XSSFWorkbook excel = new XSSFWorkbook(resourceAsStream);
            XSSFSheet sheet = excel.getSheetAt(0);

            //日期填充
            XSSFRow row = sheet.getRow(1);
            row.getCell(1).setCellValue("时间:" +begin+"至"+end);

            //第四行
            XSSFRow row1 = sheet.getRow(3);
            row1.getCell(2).setCellValue(businessData.getTurnover());
            row1.getCell(4).setCellValue(businessData.getOrderCompletionRate());
            row1.getCell(6).setCellValue(businessData.getNewUsers());

            //第五行
            row1 = sheet.getRow(4);
            row1.getCell(2).setCellValue(businessData.getValidOrderCount());
            row1.getCell(4).setCellValue(businessData.getUnitPrice());

            //每日明细
            for(int i=0;i<30;i++){
                LocalDate localDate = begin.plusDays(i);
                BusinessDataVO businessData1 = workspaceService.getBusinessData(LocalDateTime.of(localDate, LocalTime.MIN), LocalDateTime.of(localDate, LocalTime.MAX));
                XSSFRow row2 = sheet.getRow(7 + i);
                row2.getCell(1).setCellValue(localDate.toString());
                row2.getCell(2).setCellValue(businessData1.getTurnover());
                row2.getCell(3).setCellValue(businessData1.getOrderCompletionRate());
                row2.getCell(4).setCellValue(businessData1.getNewUsers());
                row2.getCell(5).setCellValue(businessData1.getOrderCompletionRate());
                row2.getCell(6).setCellValue(businessData1.getNewUsers());
            }

            ServletOutputStream outputStream = response.getOutputStream();
            excel.write(outputStream);
            outputStream.close();
            excel.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
