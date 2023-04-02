package com.cma.main.ServiceImpl;

import com.cma.main.Constants.Constants;
import com.cma.main.DAO.BillDAO;
import com.cma.main.JWT.JwtFilter;
import com.cma.main.POJO.Bill;
import com.cma.main.Service.BillService;
import com.cma.main.Utils.CafeUtils;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.io.IOUtils;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@Slf4j
public class BillServiceImpl implements BillService {

    @Autowired
    JwtFilter jwtFilter;

    @Autowired
    BillDAO billDao;

    /**
     * Generating the Bill
     *
     * @param requestMap
     * @return
     */
    @Override
    public ResponseEntity<String> generateBill(Map<String, Object> requestMap) {
        log.info("Inside Generate Bill [generateBill] method");
        try {
            if (requestMap.containsKey("name") && requestMap.containsKey("email") && requestMap.containsKey("contactNumber") && requestMap.containsKey("productDetails") && requestMap.containsKey("total")) {
                String filename;
                if (requestMap.containsKey("isGenerate") && !(Boolean) requestMap.get("isGenerate")) {
                    filename = (String) requestMap.get("uuid");
                } else {
                    filename = CafeUtils.generateBillName();
                    requestMap.put("uuid", filename);
                    insertBill(requestMap);
                }

                String data = "Name : " + requestMap.get("name") + "\n" + "Contact Number : " + requestMap.get("contactNumber") + "\n" +
                              "Email : " + requestMap.get("email") + "\n" + "Payment Method : " + requestMap.get("paymentMethod");

                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(Constants.PDF_PATH + filename + ".pdf"));

                document.open();
                setRectangle(document);

                Paragraph heading = new Paragraph("The Nanny's Kitchen" + "\n \n \n \n", getFont("Heading"));
                heading.setAlignment(Element.ALIGN_CENTER);
                document.add(heading);

                Paragraph paragraph = new Paragraph(data + "\n \n \n", getFont("Data"));
                document.add(paragraph);

                PdfPTable table = new PdfPTable(5);
                table.setWidthPercentage(100);

                addTableHeader(table);

                JSONArray jsonArray = CafeUtils.jsonArrayFromString((String) requestMap.get("productDetails"));

                for (int i = 0; i < jsonArray.length(); i++) {
                    addRow(table, CafeUtils.getMapFromJson(jsonArray.getString(i)));
                }

                document.add(table);

                Paragraph footer = new Paragraph("\n \n \nTotal : " + requestMap.get("total") + "\n \n \n \n", getFont("Data"));
                document.add(footer);

                Paragraph thanks = new Paragraph("Thank you for visiting. Enjoy your food & Have a nice day!", getFont("Thanks"));
                thanks.setAlignment(Element.ALIGN_CENTER);
                document.add(thanks);

                document.close();

                return new ResponseEntity<>("{\"uuid\":\"" + filename + "\"}", HttpStatus.OK);
            }
            return CafeUtils.getResponse(Constants.INVALID_DATA, HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponse(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Adding Rows for the data
     *
     * @param table
     * @param data
     */
    private void addRow(PdfPTable table, Map<String, Object> data) {
        log.info("Inside Add Row [addRow] method");
        table.addCell((String) data.get("name"));
        table.addCell((String) data.get("category"));
        table.addCell((String) data.get("quantity"));
        table.addCell(Double.toString((Double) data.get("price")));
        table.addCell(Double.toString((Double) data.get("total")));
    }

    /**
     * Adding Table Headers
     *
     * @param table
     */
    private void addTableHeader(PdfPTable table) {
        log.info("Inside Add Table Header [addTableHeader] method");

        Stream.of("Name", "Category", "Quantity", "Price", "SubTotal").forEach(column -> {
            PdfPCell header = new PdfPCell();
            header.setBackgroundColor(BaseColor.MAGENTA);
            header.setPhrase(new Phrase(column));
            header.setBackgroundColor(BaseColor.YELLOW);
            header.setHorizontalAlignment(Element.ALIGN_CENTER);
            header.setVerticalAlignment(Element.ALIGN_CENTER);
            table.addCell(header);
        });
    }

    /**
     * Selecting Font
     *
     * @param type
     * @return
     */
    private Font getFont(String type) {
        log.info("Inside Get Font [getFont] method");
        switch (type) {
            case "Heading":
                Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 28, BaseColor.RED);
                headerFont.setStyle(Font.BOLDITALIC);
                return headerFont;

            case "Data":
                Font data = FontFactory.getFont(FontFactory.COURIER, 12, BaseColor.BLACK);
                data.setStyle(Font.BOLD);
                return data;

            case "Thanks":
                Font thanks = FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, 15, BaseColor.BLUE);
                thanks.setStyle(Font.BOLD);
                return thanks;

            default:
                return new Font();
        }
    }

    /**
     * Setting Rectangle properties in PDF
     *
     * @param document
     * @throws DocumentException
     */
    private void setRectangle(Document document) throws DocumentException {
        log.info("Inside Set Rectangle [setRectangle] method");

        Rectangle rect = new Rectangle(577, 825, 18, 15);
        rect.enableBorderSide(1);
        rect.enableBorderSide(2);
        rect.enableBorderSide(4);
        rect.enableBorderSide(8);
        rect.setBorderColor(BaseColor.DARK_GRAY.brighter());
        rect.setBorderWidth(1);
        document.add(rect);
    }

    /**
     * Insert the Bill into the DB
     *
     * @param requestMap
     */
    private void insertBill(Map<String, Object> requestMap) {
        log.info("Inside Insert Bill [insertBill] method");

        try {
            Bill bill = new Bill();
            bill.setUuid((String) requestMap.get("uuid"));
            bill.setName((String) requestMap.get("name"));
            bill.setContactNumber((String) requestMap.get("contactNumber"));
            bill.setEmail((String) requestMap.get("email"));
            bill.setPaymentMethod((String) requestMap.get("paymentMethod"));
            bill.setProductDetails((String) requestMap.get("productDetails"));
            bill.setTotal((Integer) Integer.parseInt((String) requestMap.get("total")));
            bill.setCreatedBy(jwtFilter.getCurrentUserName());

            billDao.save(bill);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Method to get Bills
     *
     * @return
     */
    @Override
    public ResponseEntity<List<Bill>> getBill() {
        log.info("Inside Gert Bill [getBill] Method");

        try {
            List<Bill> billList = new ArrayList<>();
            if (jwtFilter.isAdmin()) {
                billList = billDao.getAllBills();
            } else {
                billList = billDao.getBillByUserName(jwtFilter.getCurrentUserName());
            }
            return new ResponseEntity<>(billList, HttpStatus.OK);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Method to get bytes of pdf to display in UI
     *
     * @param requestMap
     * @return
     */
    @Override
    public ResponseEntity<byte[]> getPdf(Map<String, Object> requestMap) {
        try {
            byte[] byteArray = new byte[0];
            if (!requestMap.containsKey("uuid")) {
                return new ResponseEntity<>(byteArray, HttpStatus.BAD_REQUEST);
            }
            if (requestMap.containsKey("uuid") && Objects.isNull(billDao.findFirstByUuid((String) requestMap.get("uuid")))) {
                return new ResponseEntity<>(byteArray, HttpStatus.BAD_REQUEST);
            }
            String currentUser = jwtFilter.getCurrentUserName();
            String filePath = Constants.PDF_PATH + requestMap.get("uuid") + ".pdf";
//            if(requestMap.get("email") == billDao.findFirstByEmail(currentUser)){
            if (CafeUtils.doesFileExist(filePath)) {
                byteArray = getByteFromPdf(filePath);
                return new ResponseEntity<>(byteArray, HttpStatus.OK);
            } else {
                requestMap.put("isGenerate", false);
                generateBill(requestMap);
                byteArray = getByteFromPdf(filePath);
                return new ResponseEntity<>(byteArray, HttpStatus.OK);
            }
//            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private byte[] getByteFromPdf(String filePath) throws Exception {
        try {
            File file = new File(filePath);
            InputStream inputStream = new FileInputStream(file);
            byte[] byteArray = IOUtils.toByteArray(inputStream);
            inputStream.close();
            return byteArray;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public ResponseEntity<String> deleteBill(String billId) {
        try {
            if (jwtFilter.isAdmin()) {
                Bill bill = billDao.findFirstById(billId);
                if (Objects.nonNull(bill)) {
                    billDao.delete(bill);
                    return CafeUtils.getResponse(Constants.BILL_DELETE_SUCCESS, HttpStatus.OK);
                }
                return CafeUtils.getResponse(Constants.NOT_EXIST, HttpStatus.NOT_FOUND);
            }
            return CafeUtils.getResponse(Constants.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponse(Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
