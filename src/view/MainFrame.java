package view;

import fileio.FileIO;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.Book;
import model.BookManager;
import model.Reader;

/**
 *
 * @author CanhND
 */
public class MainFrame extends javax.swing.JFrame {

    private static final String FILE_BOOK = "SACH.TXT";
    private static final String FILE_READER = "BD.TXT";
    private static final String FILE_BOOK_MANAGER = "QLMS.TXT";

    DefaultTableModel tableBook;
    DefaultTableModel tableReader;
    DefaultTableModel tableBookManager;

    ArrayList<Book> listBooks;
    ArrayList<Reader> listReaders;
    ArrayList<BookManager> listBookManager;
    ArrayList<Integer> listId;

    public MainFrame() {
        initComponents();
        setLocationRelativeTo(null);
        setTitle("Quản Lý Mượn Sách");

        tableBook = (DefaultTableModel) table1.getModel();
        tableReader = (DefaultTableModel) table2.getModel();
        tableBookManager = (DefaultTableModel) table3.getModel();

        loadDataToTable();
        loadIdToCbox();

        btnAdd1.addActionListener((ActionEvent e) -> {
            String bookName = txtBookName.getText();
            String bookAuthor = txtAuthor.getText();
            String bookMajor = cbox1.getItemAt(cbox1.getSelectedIndex());
            String bookYear = txtYear.getText();
            String bookAmount = txtBookAmount.getText();

            if (bookName.equals("") || bookAuthor.equals("")
                    || bookMajor.equals("") || bookYear.equals("")
                    || bookAmount.equals("")) {
                JOptionPane.showMessageDialog(null, "Nhập thiếu thông tin.");
                return;
            }

            try {
                int amout = Integer.parseInt(bookAmount);
                int pos = listBooks.size() - 1;
                if (pos != -1) {
                    Book.setCurrentId(listBooks.get(pos).getId());
                }
                Book book = new Book(bookName, bookAuthor, bookMajor, bookYear, amout);
                for (int i = 0; i < listBooks.size(); i++) {
                    if (listBooks.get(i).equals(book)) {
                        JOptionPane.showMessageDialog(null, "Trùng đầu sách.");
                        return;
                    }
                }
                tableBook.addRow(book.toObject());
                listBooks.add(book);
                loadIdToCbox();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        });

        btnAdd2.addActionListener((ActionEvent e) -> {
            String readerName = txtReaderName.getText();
            String readerAddress = txtReaderAddress.getText();
            String readerPhone = txtPhoneNum.getText();

            if (readerName.equals("") || readerAddress.equals("")
                    || readerPhone.equals("")) {
                JOptionPane.showMessageDialog(null, "Nhập thiếu thông tin.");
                return;
            }

            if (readerPhone.length() < 9 || readerPhone.length() > 10
                    || !readerPhone.startsWith("0")) {
                JOptionPane.showMessageDialog(null, "Số điện thoại không hợp lệ");
                return;
            }

            int pos = listReaders.size() - 1;
            if (pos != -1) {
                Reader.setCurrentId(listReaders.get(pos).getId());
            }
            Reader reader = new Reader(readerName, readerAddress, readerPhone);
            for (int i = 0; i < listReaders.size(); i++) {
                if (listReaders.get(i).equals(reader)) {
                    JOptionPane.showMessageDialog(null, "Bạn đọc đã tồn tại.");
                    return;
                }
            }
            tableReader.addRow(reader.toObject());
            listReaders.add(reader);
            loadIdToCbox();
        });

        btnAdd3.addActionListener((ActionEvent e) -> {
            int bookIndex = cbox2.getSelectedIndex();
            int readerIndex = cbox3.getSelectedIndex();
            String amount = cbox5.getItemAt(cbox5.getSelectedIndex());
            try {
                int a = Integer.parseInt(amount);
                Book b = listBooks.get(bookIndex);
                Reader r = listReaders.get(readerIndex);
                BookManager bm = new BookManager(b, r, a);

                //Kiểm tra số lượng cuốn sách được mượn trên một đầu sách (<= 3 cuốn).
                for (int i = 0; i < listBookManager.size(); i++) {
                    if (listBookManager.get(i).equals(bm)) {
                        int s = listBookManager.get(i).getAmount();
                        if (s + a > 3) {
                            JOptionPane.showMessageDialog(null, "Bạn được mượn tối đa 3 cuốn.");
                        } else {
                            tableBookManager.setValueAt(s + a, i, 4);
                            bm.setAmount(s + a);
                            listBookManager.remove(listBookManager.get(i));
                            listBookManager.add(bm);
                        }
                        return;
                    }
                }
                
                //Kiểm tra số lượng đầu sách tối đa được mượn.
                int counter = getId(bm.getReader().getId()) + 1;
                if (counter > 5) {
                    JOptionPane.showMessageDialog(null, "Bạn được mượn tối đã 5 đầu sách.");
                } else {
                    tableBookManager.addRow(bm.toObject());
                    listBookManager.add(bm);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Nhập sai định dạng số lượng.");
            }
        });

        saveToFile(btnSave1, listBooks, FILE_BOOK);
        saveToFile(btnSave2, listReaders, FILE_READER);
        saveToFile(btnSave3, listBookManager, FILE_BOOK_MANAGER);

        //Sắp xếp.
        cbox4.addActionListener((ActionEvent e) -> {
            String opt = cbox4.getItemAt(cbox4.getSelectedIndex());
            if (opt.equals("Sắp xếp theo tên bạn đọc")) {
                Comparator<BookManager> cmp = (BookManager bm1, BookManager bm2) -> {
                    String name1 = bm1.getReader().getName();
                    String name2 = bm2.getReader().getName();
                    return name1.compareTo(name2);
                };
                Collections.sort(listBookManager, cmp);
            } else {
                Collections.sort(listBookManager, Comparator.comparing(BookManager::getAmount).reversed());
            }
            tableBookManager.setRowCount(0);
            listBookManager.forEach((bm) -> {
                tableBookManager.addRow(bm.toObject());
            });
        });

        //Tìm kiếm
        btnSearch.addActionListener((ActionEvent e) -> {
            String readerName = txtSearch1.getText();
            String bookName = txtSearch2.getText();
            if (readerName.equals("") && bookName.equals("")) {
                JOptionPane.showMessageDialog(null, "Nhập thiếu thông tin.");
                return;
            }
            tableBookManager.setRowCount(0);
            for (BookManager bm : listBookManager) {
                if (bm.getReader().getName().contains(readerName)
                        && bm.getBook().getBookName().contains(bookName)) {
                    tableBookManager.addRow(bm.toObject());
                }
            }
        });
    }

    private void loadIdToCbox() {
        cbox2.removeAllItems();
        cbox3.removeAllItems();
        listBooks.forEach((b) -> {
            cbox2.addItem(b.getId() + "");
        });
        listReaders.forEach((r) -> {
            cbox3.addItem(r.getId() + "");
        });
    }

    //Trả về số lượng đầu sách một bạn đã mượn dựa trên id của bạn đó.
    private int getId(int id) {
        int counter = 0;
        listId = new ArrayList<>();
        for (BookManager bm : listBookManager) {
            listId.add(bm.getReader().getId());
        }
        for (Integer i : listId) {
            if (i == id) {
                counter++;
            }
        }
        return counter;
    }

    private void loadDataToTable() {
        listBooks = new ArrayList<>();
        listReaders = new ArrayList<>();
        listBookManager = new ArrayList<>();
        FileIO.readFromFile(listBooks, FILE_BOOK);
        FileIO.readFromFile(listReaders, FILE_READER);
        FileIO.readFromFile(listBookManager, FILE_BOOK_MANAGER);
        listBooks.forEach((b) -> {
            tableBook.addRow(b.toObject());
        });
        listReaders.forEach((r) -> {
            tableReader.addRow(r.toObject());
        });
        listBookManager.forEach((bm) -> {
            tableBookManager.addRow(bm.toObject());
        });
    }

    private void saveToFile(JButton btn, ArrayList list, String file) {
        btn.addActionListener((ActionEvent e) -> {
            FileIO.writeToFile(list, file);
            JOptionPane.showMessageDialog(null, "Đã Lưu.");
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtBookName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtAuthor = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtYear = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtBookAmount = new javax.swing.JTextField();
        btnAdd1 = new javax.swing.JButton();
        btnSave1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        table1 = new javax.swing.JTable();
        cbox1 = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        txtReaderName = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtReaderAddress = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtPhoneNum = new javax.swing.JTextField();
        btnAdd2 = new javax.swing.JButton();
        btnSave2 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        table2 = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        cbox2 = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        cbox3 = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        btnAdd3 = new javax.swing.JButton();
        btnSave3 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        table3 = new javax.swing.JTable();
        cbox4 = new javax.swing.JComboBox<>();
        txtSearch1 = new javax.swing.JTextField();
        txtSearch2 = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        cbox5 = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Tên sách");

        jLabel2.setText("Tác Giả");

        jLabel3.setText("Chuyên Ngành");

        jLabel4.setText("Năm xuất bản");

        jLabel5.setText("Số Lượng");

        btnAdd1.setText("THÊM");

        btnSave1.setText("LƯU VÀO FILE");

        table1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Mã Sách", "Tên Sách", "Tác Giả", "Chuyên Ngành", "Năm XB", "Số Lượng"
            }
        ));
        jScrollPane1.setViewportView(table1);

        cbox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Khoa học tự nhiên", "Văn học – Nghệ thuật", "Điện tử Viễn thông", "Công nghệ thông tin" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel5)
                    .addComponent(txtBookAmount, javax.swing.GroupLayout.DEFAULT_SIZE, 187, Short.MAX_VALUE)
                    .addComponent(jLabel4)
                    .addComponent(txtYear, javax.swing.GroupLayout.DEFAULT_SIZE, 187, Short.MAX_VALUE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2)
                    .addComponent(txtAuthor, javax.swing.GroupLayout.DEFAULT_SIZE, 187, Short.MAX_VALUE)
                    .addComponent(jLabel1)
                    .addComponent(txtBookName)
                    .addComponent(btnAdd1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSave1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cbox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(37, 37, 37)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 664, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(30, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 478, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtBookName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtAuthor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtBookAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(53, 53, 53)
                        .addComponent(btnAdd1)
                        .addGap(18, 18, 18)
                        .addComponent(btnSave1)))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Sách", jPanel1);

        jLabel6.setText("Tên bạn đọc");

        jLabel7.setText("Địa chỉ");

        jLabel8.setText("Số điện thoại");

        btnAdd2.setText("THÊM");

        btnSave2.setText("LƯU VÀO FILE");

        table2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Mã Bạn Đọc", "Tên Bạn Đọc", "Địa Chỉ", "Số Điện Thoại"
            }
        ));
        jScrollPane2.setViewportView(table2);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel8)
                    .addComponent(txtPhoneNum, javax.swing.GroupLayout.DEFAULT_SIZE, 187, Short.MAX_VALUE)
                    .addComponent(jLabel7)
                    .addComponent(txtReaderAddress, javax.swing.GroupLayout.DEFAULT_SIZE, 187, Short.MAX_VALUE)
                    .addComponent(jLabel6)
                    .addComponent(txtReaderName)
                    .addComponent(btnAdd2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSave2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(37, 37, 37)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 664, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(30, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 478, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtReaderName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtReaderAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPhoneNum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(64, 64, 64)
                        .addComponent(btnAdd2)
                        .addGap(18, 18, 18)
                        .addComponent(btnSave2)))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Bạn Đọc", jPanel2);

        jLabel9.setText("Chọn mã sách:");

        jLabel10.setText("Chọn mã bạn đọc:");

        jLabel11.setText("Số lượng mượn:");

        btnAdd3.setText("THÊM");

        btnSave3.setText("LƯU VÀO FILE");

        table3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Mã sách", "Tên sách", "Mã BĐ", "Tên BĐ", "Số lượng "
            }
        ));
        jScrollPane3.setViewportView(table3);

        cbox4.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Sắp xếp theo tên bạn đọc", "Sắp xếp theo số lượng mượn." }));

        txtSearch1.setText("Nhập tên bạn đọc ....");

        txtSearch2.setText("Nhập tên sách ....");

        btnSearch.setText("Tìm Kiếm");

        cbox5.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3" }));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel11)
                    .addComponent(jLabel10)
                    .addComponent(cbox3, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel9)
                    .addComponent(cbox2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnAdd3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSave3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cbox4, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtSearch1)
                    .addComponent(txtSearch2)
                    .addComponent(btnSearch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cbox5, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(50, 50, 50)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 662, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 471, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbox5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(25, 25, 25)
                        .addComponent(btnAdd3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSave3)
                        .addGap(39, 39, 39)
                        .addComponent(cbox4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(39, 39, 39)
                        .addComponent(txtSearch1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSearch2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSearch)))
                .addContainerGap(30, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Quản Lý Mượn Sách", jPanel3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String args[]) {
        MainFrame frame = new MainFrame();
        frame.setVisible(true);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd1;
    private javax.swing.JButton btnAdd2;
    private javax.swing.JButton btnAdd3;
    private javax.swing.JButton btnSave1;
    private javax.swing.JButton btnSave2;
    private javax.swing.JButton btnSave3;
    private javax.swing.JButton btnSearch;
    private javax.swing.JComboBox<String> cbox1;
    private javax.swing.JComboBox<String> cbox2;
    private javax.swing.JComboBox<String> cbox3;
    private javax.swing.JComboBox<String> cbox4;
    private javax.swing.JComboBox<String> cbox5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable table1;
    private javax.swing.JTable table2;
    private javax.swing.JTable table3;
    private javax.swing.JTextField txtAuthor;
    private javax.swing.JTextField txtBookAmount;
    private javax.swing.JTextField txtBookName;
    private javax.swing.JTextField txtPhoneNum;
    private javax.swing.JTextField txtReaderAddress;
    private javax.swing.JTextField txtReaderName;
    private javax.swing.JTextField txtSearch1;
    private javax.swing.JTextField txtSearch2;
    private javax.swing.JTextField txtYear;
    // End of variables declaration//GEN-END:variables
}
