package com.frpr.helper;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.frpr.model.ProviderRegistry;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

public class ExcelHelper {

    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    static String SHEET = "provider";

    public static boolean hasExcelFormat(MultipartFile file) {

        if (!TYPE.equals(file.getContentType())) {
            return false;
        }

        return true;
    }

    public static List<ProviderRegistry> excelToProviderRegistry(InputStream is) {
        // System.out.println("1. Now trying to the File information!");
        try {
            Workbook workbook = new XSSFWorkbook(is);
            // System.out.println("2. Now trying to the File information!");
            Sheet sheet = workbook.getSheet(SHEET);
            // System.out.println("3. Now trying to the File information!");
            Iterator<Row> rows = sheet.iterator();
            // System.out.println("4. Now trying to the File information!");

            List<ProviderRegistry> providerRegistries = new ArrayList<ProviderRegistry>();

            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();
                // System.out.println("5. Now trying to the File information!");
                // skip header
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }
                // System.out.println("6. Now trying to the File information!");
                Iterator<Cell> cellsInRow = currentRow.iterator();
                // System.out.println("7. Now trying to the File information!");
                ProviderRegistry providerRegistry = new ProviderRegistry();

                int cellIdx = 0;
                while (cellsInRow.hasNext()) {
                    Cell currentCell = cellsInRow.next();
                    // System.out.println("8. Now trying to the File information!");
                    switch (cellIdx) {
                        case 0:
                            // Here We need to generate the UUID for the Provider

                            break;
                        case 1:
                            if (currentCell.getCellTypeEnum() == CellType.STRING)

                                providerRegistry.setDocumentType(currentCell.getStringCellValue());

                            else if (currentCell.getCellTypeEnum() == CellType.NUMERIC)
                                providerRegistry.setDocumentType(String.valueOf(currentCell.getNumericCellValue()));

                            break;
                        case 2:
                            currentCell.setCellType(CellType.STRING);
                            if (currentCell.getCellTypeEnum() == CellType.STRING)

                                providerRegistry.setDocumentNumber(currentCell.getStringCellValue());

                            else if (currentCell.getCellTypeEnum() == CellType.NUMERIC)
                                providerRegistry.setDocumentNumber(String.valueOf(currentCell.getNumericCellValue()));
                            break;
                        /*case 3:
                            System.out.println("############################ above : "+currentCell.getStringCellValue());
                            providerRegistry.setIdentifier(String.valueOf(currentCell.getNumericCellValue()));
                            break;*/
                        case 4:
                            if (currentCell.getCellTypeEnum() == CellType.NUMERIC)
                                providerRegistry.setPostNames(String.valueOf(currentCell.getNumericCellValue()));

                            else if (currentCell.getCellTypeEnum() == CellType.STRING)
                                providerRegistry.setPostNames(currentCell.getStringCellValue());

                            break;
                        case 5:
                            if (currentCell.getCellTypeEnum() == CellType.NUMERIC)
                                providerRegistry.setDateOfBirth(String.valueOf(currentCell.getNumericCellValue()));
                            else if (currentCell.getCellTypeEnum() == CellType.STRING)
                                providerRegistry.setDateOfBirth(currentCell.getStringCellValue());
                            break;

                        case 6:
                            if (currentCell.getCellTypeEnum() == CellType.STRING)

                                providerRegistry.setMaritalStatus(currentCell.getStringCellValue().toUpperCase());

                            else if (currentCell.getCellTypeEnum() == CellType.NUMERIC)
                                providerRegistry.setMaritalStatus(String.valueOf(currentCell.getNumericCellValue()).toUpperCase());

                            break;
                        case 7:
                            if (currentCell.getCellTypeEnum() == CellType.STRING)
                                providerRegistry.setSex(currentCell.getStringCellValue().toUpperCase());

                            else if (currentCell.getCellTypeEnum() == CellType.NUMERIC)

                                providerRegistry.setSex(String.valueOf(currentCell.getNumericCellValue()).toUpperCase());

                            break;
                        case 8:
                            if (currentCell.getCellTypeEnum() == CellType.STRING)
                                providerRegistry.setNationality(currentCell.getStringCellValue());

                            else if (currentCell.getCellTypeEnum() == CellType.NUMERIC)
                                providerRegistry.setNationality(String.valueOf(currentCell.getNumericCellValue()));

                            break;
                        case 9:
                            if (currentCell.getCellTypeEnum() == CellType.STRING)
                                providerRegistry.setDomicileCountry(currentCell.getStringCellValue());

                            else if (currentCell.getCellTypeEnum() == CellType.NUMERIC)
                                providerRegistry.setDomicileCountry(String.valueOf(currentCell.getNumericCellValue()));
                            break;
                        case 10:
                            if (currentCell.getCellTypeEnum() == CellType.STRING)
                                providerRegistry.setDomicileProvince(currentCell.getStringCellValue());
                            else if (currentCell.getCellTypeEnum() == CellType.NUMERIC)
                                providerRegistry.setDomicileProvince(String.valueOf(currentCell.getNumericCellValue()));
                            break;
                        case 11:
                            if (currentCell.getCellTypeEnum() == CellType.STRING)
                                providerRegistry.setDomicileDistrict(currentCell.getStringCellValue());
                            else if (currentCell.getCellTypeEnum() == CellType.NUMERIC)
                                providerRegistry.setDomicileDistrict(String.valueOf(currentCell.getNumericCellValue()));
                            break;
                        case 12:
                            if (currentCell.getCellTypeEnum() == CellType.STRING)
                                providerRegistry.setDomicileSector(currentCell.getStringCellValue());
                            else if (currentCell.getCellTypeEnum() == CellType.NUMERIC)
                                providerRegistry.setDomicileSector(String.valueOf(currentCell.getNumericCellValue()));
                            break;
                        case 13:
                            if (currentCell.getCellTypeEnum() == CellType.STRING)
                                providerRegistry.setDomicileCell(currentCell.getStringCellValue());
                            if (currentCell.getCellTypeEnum() == CellType.NUMERIC)
                                providerRegistry.setDomicileCell(String.valueOf(currentCell.getNumericCellValue()));
                            break;
                        case 14:
                            if (currentCell.getCellTypeEnum() == CellType.STRING)
                                providerRegistry.setDomicileVillage(currentCell.getStringCellValue());
                            else if (currentCell.getCellTypeEnum() == CellType.NUMERIC)
                                providerRegistry.setDomicileVillage(String.valueOf(currentCell.getNumericCellValue()));
                            break;
                        case 15:
                            currentCell.setCellType(CellType.STRING);
                            if (currentCell.getCellTypeEnum() == CellType.NUMERIC){
                               // providerRegistry.setLicenseNumber(String.valueOf(currentCell.getNumericCellValue()));
                                Double dat2 = currentCell.getNumericCellValue();
                                int i1 = dat2.intValue();
                                String datastr1 = String.valueOf(i1);
                                providerRegistry.setLicenseNumber(datastr1);
                            }
                                
                            else if (currentCell.getCellTypeEnum() == CellType.STRING)
                                providerRegistry.setLicenseNumber(currentCell.getStringCellValue());
                            System.out.println(currentCell.getCellTypeEnum());
                            break;
                        case 16:
                            providerRegistry.setLicenseNumber(currentCell.getStringCellValue());
                            break;
                        case 17:
                            if (currentCell.getCellTypeEnum() == CellType.STRING)
                                providerRegistry.setEmail(currentCell.getStringCellValue());
                            else if (currentCell.getCellTypeEnum() == CellType.NUMERIC)
                                providerRegistry.setEmail(String.valueOf(currentCell.getNumericCellValue()));
                            break;
                        case 18:
                            currentCell.setCellType(CellType.STRING);
                            if (currentCell.getCellTypeEnum() == CellType.STRING)
                                providerRegistry.setPhoneNumber(currentCell.getStringCellValue());
                            else if (currentCell.getCellTypeEnum() == CellType.NUMERIC)
                                providerRegistry.setPhoneNumber(String.valueOf(currentCell.getNumericCellValue()));
                            break;
                        case 19:
                        if (currentCell.getCellTypeEnum() == CellType.STRING)
                        providerRegistry.setStatus(currentCell.getStringCellValue().toUpperCase());
                        else if (currentCell.getCellTypeEnum() == CellType.NUMERIC)
                        providerRegistry.setStatus(String.valueOf(currentCell.getNumericCellValue()).toUpperCase());
                            break;
                       /* case 20:
                            if (currentCell.getCellTypeEnum() == CellType.STRING)
                                providerRegistry.setDateOfIssue(currentCell.getStringCellValue());
                            else if (currentCell.getCellTypeEnum() == CellType.NUMERIC)
                                providerRegistry.setDateOfIssue(String.valueOf(currentCell.getNumericCellValue()));
                            break;
                        case 21:
                            if (currentCell.getCellTypeEnum() == CellType.STRING)
                                providerRegistry.setDateOfExpiry(currentCell.getStringCellValue());
                            if (currentCell.getCellTypeEnum() == CellType.NUMERIC)
                                providerRegistry.setDateOfExpiry(String.valueOf(currentCell.getNumericCellValue()));
                            break;
                        case 22:
                            if (currentCell.getCellTypeEnum() == CellType.STRING)
                                providerRegistry.setPlaceOfIssue(currentCell.getStringCellValue());
                            else if (currentCell.getCellTypeEnum() == CellType.NUMERIC)
                                providerRegistry.setPlaceOfIssue(String.valueOf(currentCell.getNumericCellValue()));
                            break;*/
                        case 20:
                            if (currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                                Double dat1 = currentCell.getNumericCellValue();
                                int i1 = dat1.intValue();
                                String datastr1 = String.valueOf(i1);
                                providerRegistry.setApplicationNumber(datastr1);
                            } else if (currentCell.getCellTypeEnum() == CellType.STRING)
                                providerRegistry.setApplicationNumber(currentCell.getStringCellValue());
                            break;
                        case 21:
                            currentCell.setCellType(CellType.STRING);
                            if (currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                                Double Nindbl = currentCell.getNumericCellValue();
                                int Ninint = Nindbl.intValue();
                                String Ninstr = String.valueOf(Ninint);
                                providerRegistry.setNin(Ninstr);
                            } else if (currentCell.getCellTypeEnum() == CellType.STRING)
                                providerRegistry.setNin(currentCell.getStringCellValue());
                            break;
                        case 22:
                            currentCell.setCellType(CellType.STRING);
                            if (currentCell.getCellTypeEnum() == CellType.STRING)
                                providerRegistry.setNid(currentCell.getStringCellValue());
                            else if (currentCell.getCellTypeEnum() == CellType.NUMERIC)
                                providerRegistry.setNid(String.valueOf(currentCell.getNumericCellValue()));
                            break;
                        case 23:
                            if (currentCell.getCellTypeEnum() == CellType.STRING)
                                providerRegistry.setFatherName(currentCell.getStringCellValue());
                            else if (currentCell.getCellTypeEnum() == CellType.NUMERIC)
                                providerRegistry.setFatherName(String.valueOf(currentCell.getNumericCellValue()));
                            break;
                        case 24:
                            providerRegistry.setApplicationNumber(currentCell.getStringCellValue());
                            break;
                        case 25:
                            providerRegistry.setNin(currentCell.getStringCellValue()); 
                            break;

                        // villageId civilStatus spouse applicantType licenceExpiryDate licence_status
                        // FacilityId Facilities
                        case 26:
                            providerRegistry.setNid(currentCell.getStringCellValue());
                            break;
                        case 27:
                            if (currentCell.getCellTypeEnum() == CellType.STRING)
                                providerRegistry.setCivilStatus(currentCell.getStringCellValue());
                            else if (currentCell.getCellTypeEnum() == CellType.NUMERIC)
                                providerRegistry.setCivilStatus(String.valueOf(currentCell.getNumericCellValue()));
                            break;
                        case 28:
                            if (currentCell.getCellTypeEnum() == CellType.STRING)
                                providerRegistry.setSpouse(currentCell.getStringCellValue());
                            if (currentCell.getCellTypeEnum() == CellType.NUMERIC)
                                providerRegistry.setSpouse(String.valueOf(currentCell.getNumericCellValue()));
                            break;
                        case 29:
                            if (currentCell.getCellTypeEnum() == CellType.STRING)
                                providerRegistry.setApplicantType(currentCell.getStringCellValue());
                            else if (currentCell.getCellTypeEnum() == CellType.NUMERIC)
                                providerRegistry.setApplicantType(String.valueOf(currentCell.getNumericCellValue()));
                            break;
                        case 30:
                            if (currentCell.getCellTypeEnum() == CellType.STRING) {
                                String strDate = String.valueOf(currentCell.getStringCellValue());
                                try {
                                    Date date1 = new SimpleDateFormat("yyyy/MM/dd").parse(strDate);
                                    providerRegistry.setLicenseExpiryDate(date1);
                                } catch (Throwable throwable) {
                                    System.out.println("failed to convert dates");
                                }
                            } else if (currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                                String strDate = String.valueOf(currentCell.getNumericCellValue());
                                try {
                                    Date date1 = new SimpleDateFormat("yyyy/MM/dd").parse(strDate);
                                    providerRegistry.setLicenseExpiryDate(date1);
                                } catch (Throwable throwable) {
                                    System.out.println("failed to convert dates");
                                }
                            }
                            break;
                        case 31:
                            if (currentCell.getCellTypeEnum() == CellType.STRING)
                                providerRegistry.setLicense_status(currentCell.getStringCellValue());
                            else if (currentCell.getCellTypeEnum() == CellType.NUMERIC)
                                providerRegistry.setLicense_status(String.valueOf(currentCell.getNumericCellValue()));
                            break;
                        case 32:
                            if (currentCell.getCellTypeEnum() == CellType.NUMERIC) {

                                Double fiddbl = currentCell.getNumericCellValue();
                                int fidint = fiddbl.intValue();
                                String fidstr = String.valueOf(fidint);
                                providerRegistry.setFacilityId(Arrays.asList(fidstr.split(",")));
                            }

                            else if (currentCell.getCellTypeEnum() == CellType.STRING)
                                providerRegistry.setFacilityId(
                                        Arrays.asList((currentCell.getStringCellValue()).split(",")));

                            break;
                        case 33:
                            providerRegistry.setApplicantType(currentCell.getStringCellValue());
                            break;
                        case 34:
                        
                            try {
                                String strDate=String.valueOf(currentCell.getNumericCellValue()); 
                                Date date1=new SimpleDateFormat("dd/MM/yyyy").parse(strDate);
                                providerRegistry.setLicenseExpiryDate(date1);
                            } catch(Throwable throwable) {
                                System.out.println("failed to convert dates");
                            }
                    
                            break;
                        case 35:
                        providerRegistry.setLicense_status(currentCell.getStringCellValue());
                        break;
                        /*case 36:
                            Double fiddbl=currentCell.getNumericCellValue();
                            int fidint=fiddbl.intValue();
                            String fidstr=String.valueOf(fidint);
                            providerRegistry.setFacilityId(Arrays.asList(fidstr.split(",")));
                            break;*/
                        
                        default:
                            //Here the Comming Field can not be mapped to the right database field.
                            break;
                    }
                    // System.out.println("9. Now trying to the File information!" + cellIdx);
                    cellIdx++;
                }
                String pd = providerRegistry.getDocumentNumber();
                // System.out.println(pd);
                // if(providerRegistry.getDocumentNumber()){
                if (pd == null || pd.length() == 0) {

                } else {

                    providerRegistries.add(providerRegistry);
                }
            }
            // System.out.println("10. Now trying to the File information!");
            workbook.close();

            return providerRegistries;
        } catch (IOException e) {
            // System.out.println(e.getMessage());
            throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
        }
    }

}
