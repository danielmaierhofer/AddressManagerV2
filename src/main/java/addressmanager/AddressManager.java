/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package addressmanager;

import addressmanager.dao.ServiceException;
import addressmanager.entity.Address;
import addressmanager.service.AddressJDBCService;
import addressmanager.service.AddressService;
import addressmanager.service.DATABASE;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 *
 * @author Daniel
 */
public class AddressManager extends JFrame {

    private final static Logger logger = Logger.getLogger(AddressManager.class);

    private InputPanel inputPanel;
    private ListPanel listPanel;
    private MessagePanel messagePanel;
    private ButtonPanel buttonPanel;

    public static void main(String[] args) {
        logger.setLevel(Level.INFO);
        String pattern = "%m %n";
        PatternLayout layout = new PatternLayout(pattern);
        ConsoleAppender appender = new ConsoleAppender(layout);
        logger.addAppender(appender);
        new AddressManager(DATABASE.HSQLDB);
    }

    public AddressManager(DATABASE database) {
        super("Adressverwaltung");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        inputPanel = new InputPanel(this);
        add(inputPanel, BorderLayout.NORTH);

        listPanel = new ListPanel(this, database);
        add(listPanel);

//        customers = new JList<>(new DefaultListModel<String>());
//        customers = new List();
//        add(customers, BorderLayout.CENTER);
//        customers.addItemListener(this);
//        customers.addMouseListener(new MouseAdapter() {
//
//            @Override
//            public void mouseClicked(MouseEvent evt) {
//                if (evt.getClickCount() == 1) {
//                    clearFields();
//                    changeButton.setEnabled(false);
//                }
//                if (evt.getClickCount() == 2) {
//                    StringTokenizer data = new StringTokenizer(customers.getSelectedItem());
//                    firstname.setText(data.nextToken(";"));
//                    lastname.setText(data.nextToken(";"));
//                    address.setText(data.nextToken(";"));
//                    changeButton.setEnabled(true);
//                }
//            }
//
//        });
        JPanel southPanel = new JPanel(new BorderLayout());
        messagePanel = new MessagePanel();
        buttonPanel = new ButtonPanel(this);
        southPanel.add(messagePanel, BorderLayout.NORTH);
        southPanel.add(buttonPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

        pack();
        setVisible(true);
    }

//    public void saveItems() {
//        java.util.List<Address> addressList = new ArrayList<>();
//        for (String data : list.getItems()) {
//            StringTokenizer address = new StringTokenizer(data);
//            String vorname = address.nextToken(";");
//            String nachname = address.nextToken(";");
//            String addresse = address.nextToken(";");
//            addressList.add(new Address(1, vorname, nachname, addresse));
//        }
//        try {
//            addressService.saveItems(addressList);
//            msgField.setText("Addressen erfolgreich gespeichert!");
//        } catch (DAOSysException ex) {
//            msgField.setText(ex.getMessage());
//            java.util.logging.Logger.getLogger(AddressManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//    }
//
//    public void loadItems() {
//        try {
//            AddressManagerModel a = (AddressManagerModel) addressManagerJTable.getModel();
//            a.insertData(addressService.loadItems());
////            customers.removeAll();
////            for (Address data : addressList) {
////                String vorname = data.getFirstname();
////                String nachname = data.getLastname();
////                String addresse = data.getAddress();
////                customers.add(vorname + ";" + nachname + ";" + addresse);
////            }
//            msgField.setText("Addressen erfolgreich geladen!");
//        } catch (DAOSysException ex) {
//            msgField.setText(ex.getMessage());
//            java.util.logging.Logger.getLogger(AddressManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//    }
    public InputPanel getInputPanel() {
        return inputPanel;
    }

    public MessagePanel getMessagePanel() {
        return messagePanel;
    }

    public ButtonPanel getButtonPanel() {
        return buttonPanel;
    }

    public ListPanel getListPanel() {
        return listPanel;
    }

    public void displayMessage(String message) {
        getMessagePanel().setMessage(message);
    }

    public class AddressManagerModel extends AbstractTableModel {

        private java.util.List<Address> customers = new ArrayList<>();
        private AddressManager addressManager;
        private AddressService addressService;

        public AddressManagerModel(AddressManager addressManager, DATABASE database) {
            this.addressManager = addressManager;
            addressService = new AddressJDBCService(database);
            try {
                customers = addressService.findAll();
            } catch (ServiceException ex) {
                java.util.logging.Logger.getLogger(AddressManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        }

        @Override
        public int getRowCount() {
            return customers.size();
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public String getColumnName(int c) {
            String[] headers = {"Vorname", "Nachname", "Adresse"};
            return headers[c];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return getValueAt(0, columnIndex).getClass();
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return true;
        }

        public void addRow(Address address) {
            try {
                if (addressService.findAll().size() > 0) {
                    address.setId(addressService.getAddressList().get(addressService.findAll().size() - 1).getId() + 1);
                }
            } catch (ServiceException ex) {
                java.util.logging.Logger.getLogger(AddressManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }

            customers.add(address);
            insertData(customers);
            try {
                addressService.insert(address);
            } catch (ServiceException ex) {
                java.util.logging.Logger.getLogger(AddressManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        }

        public void updateRow(Address address) {
            try {
                addressService.update(address);
                customers = addressService.findAll();
                fireTableDataChanged();
            } catch (ServiceException ex) {
                java.util.logging.Logger.getLogger(AddressManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        }

        public void deleteRow(int id) {
            try {
                customers.remove(addressService.findById(id));
                addressService.delete(id);
                System.out.println(id);
                fireTableDataChanged();
            } catch (ServiceException ex) {
                java.util.logging.Logger.getLogger(AddressManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        }

        public Address getRow(int index) {
            return customers.get(index);
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return customers.get(rowIndex).getFirstname();
                case 1:
                    return customers.get(rowIndex).getLastname();
                case 2:
                    return customers.get(rowIndex).getAddress();
                default:
                    return "Fehler";
            }
        }

        public void setValueAt(Object value, int rowIndex, int columnIndex) {
            Address adr = customers.get(rowIndex);
            String data = (String) value;
            switch (columnIndex) {
                case 0:
                    adr.setFirstname(data);
                    customers.set(rowIndex, adr);
                     {
                        try {
                            addressService.update(adr);
                        } catch (ServiceException ex) {
                            java.util.logging.Logger.getLogger(AddressManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                        }
                    }
                    break;
                case 1:
                    adr.setLastname(data);
                    customers.set(rowIndex, adr);
                     {
                        try {
                            addressService.update(adr);
                        } catch (ServiceException ex) {
                            java.util.logging.Logger.getLogger(AddressManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                        }
                    }
                    break;
                case 2:
                    adr.setAddress(data);
                    customers.set(rowIndex, adr);
                     {
                        try {
                            addressService.update(adr);
                        } catch (ServiceException ex) {
                            java.util.logging.Logger.getLogger(AddressManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                        }
                    }
                    break;
            }
            fireTableCellUpdated(rowIndex, columnIndex);
        }

        public void insertData(java.util.List<Address> address) {
            customers = address;
            fireTableDataChanged();
        }
    }

    public class MessagePanel extends JPanel {

        private JTextField msgField;

        public MessagePanel() {
            msgField = new JTextField("", 40);
            msgField.setEditable(false);
            add(msgField);
        }

        public void setMessage(String message) {
            msgField.setText(message);
        }
    }

    public class InputPanel extends JPanel {

        private AddressManager addressManager;
        private JLabel lblFirstname;
        private JLabel lblLastname;
        private JLabel lblAddress;
        private JTextField firstname;
        private JTextField lastname;
        private JTextField address;

        public InputPanel(AddressManager addressManager) {
            setLayout(new GridLayout(3, 2));

            this.addressManager = addressManager;
            lblFirstname = new JLabel("Vorname");
            lblLastname = new JLabel("Familienname");
            lblAddress = new JLabel("Adresse");

            firstname = new JTextField();
            lastname = new JTextField();
            address = new JTextField();

            add(lblFirstname);
            add(firstname);
            add(lblLastname);
            add(lastname);
            add(lblAddress);
            add(address);
        }

        public void clearFields() {
            if (!firstname.getText().equals("")) {
                firstname.setText("");
                lastname.setText("");
                address.setText("");
            }
        }

        public boolean validateListEntry() {
            return validateField(firstname, lblFirstname) && validateField(lastname, lblLastname) && validateField(address, lblAddress);
        }

        private boolean validateField(JTextField field, JLabel label) {
            if (field.getText().isEmpty()) {
                addressManager.getMessagePanel().setMessage("Bitte " + label.getText() + " eingeben");
                field.requestFocusInWindow();
                return false;
            }
            return true;
        }

        public Address createListEntry() {
            return new Address(1, firstname.getText(), lastname.getText(), address.getText());
        }

        public void displayListEntry(Address addr) {
            firstname.setText(addr.getFirstname());
            lastname.setText(addr.getLastname());
            address.setText(addr.getAddress());
        }
    }

    public class ButtonPanel extends JPanel implements ActionListener {

        private AddressManager addressManager;
        private JButton cancelButton;
        private JButton deleteButton;
        private JButton changeButton;
        private JButton newButton;

        public ButtonPanel(AddressManager addressManager) {
            this.addressManager = addressManager;
            cancelButton = new JButton("Abbrechen");
            cancelButton.setName("cancelButton");
            cancelButton.addActionListener(this);
            deleteButton = new JButton("Löschen");
            deleteButton.setName("deleteButton");
            deleteButton.addActionListener(this);
            changeButton = new JButton("Ändern");
            changeButton.setName("changeButton");
            changeButton.addActionListener(this);
            changeButton.setEnabled(false);
            newButton = new JButton("Neu");
            newButton.setName("newButton");
            newButton.addActionListener(this);

            setLayout(new FlowLayout(FlowLayout.RIGHT));
            add(cancelButton);
            add(deleteButton);
            //add(changeButton);
            add(newButton);

        }

        public void enableChangeButton(boolean state) {
            changeButton.setEnabled(state);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JButton button = (JButton) e.getSource();

            switch (button.getName()) {
                case "newButton": {
                    if (addressManager.getInputPanel().validateListEntry() == true) {
                        addressManager.getListPanel().addAddress(addressManager.getInputPanel().createListEntry());
                        logger.info("Adresse hinzugefügt");
                    }
                    break;
                }
                case "changeButton": {
                    if (addressManager.getInputPanel().validateListEntry() == true) {
                        addressManager.getListPanel().updateAddress(addressManager.getInputPanel().createListEntry());
                        changeButton.setEnabled(false);
                        logger.info("Adresse geändert");
                    }
                    break;
                }
                case "deleteButton": {
                    int selectedRow = addressManager.getListPanel().addressManagerJTable.getSelectedRow();
                    Address adr = addressManager.getListPanel().addressManagerModel.getRow(selectedRow);
                    addressManager.getListPanel().deleteAddress(adr);
                    logger.info("Adresse gelöscht");
                    break;
                }
                case "cancelButton": {
                    System.exit(0);
                    break;
                }
            }

        }
    }

    public class ListPanel extends JPanel {

        private AddressManager addressManager;
        protected AddressManagerModel addressManagerModel;
        protected JTable addressManagerJTable;

        public ListPanel(AddressManager addrManager, DATABASE database) {
            this.addressManager = addrManager;
            addressManagerModel = new AddressManagerModel(addrManager, database);
            addressManagerJTable = new JTable(addressManagerModel);

            addressManagerJTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                @Override
                public void valueChanged(ListSelectionEvent e) {
                    addressManager.getButtonPanel().enableChangeButton(true);
                    int selectedRow = addressManagerJTable.getSelectedRow();
                    if (selectedRow != -1) {
                        //addressManager.getInputPanel().displayListEntry(addressManagerModel.getRow(selectedRow));
                    }
                }
            });

            JScrollPane scrollPane = new JScrollPane(addressManagerJTable);
            add(scrollPane);
        }

        public void addAddress(Address address) {
            addressManagerModel.addRow(address);
            addressManager.getMessagePanel().setMessage("Kunde " + address.getFirstname() + " " + address.getLastname() + " wurde hinzugefügt");
        }

        public void deleteAddress(Address address) {
            addressManagerModel.deleteRow(address.getId());
            addressManager.getMessagePanel().setMessage("Kunde " + address.getFirstname() + " " + address.getLastname() + " wurde gelöscht");
        }

        public void updateAddress(Address address) {
            addressManagerModel.updateRow(address);
            addressManager.getMessagePanel().setMessage("Kunde " + address.getFirstname() + " " + address.getLastname() + " wurde geändert");
        }
    }
}
