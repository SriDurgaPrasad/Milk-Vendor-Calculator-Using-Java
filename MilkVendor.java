import java.util.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MilkVendor {
    private String name;
    private int litres;
    private float fatReading, price;
    private String dateTime;
    
    private static final String FILE = "MilkVendorData_Master.csv";
    private static final String HEADER = "S.No,Vendor Name,Date,Time,Litres,Fat Reading (%),Price per Litre (₹),Total Price (₹)";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    
    public void getMilkVendorInfo(Scanner s) {
        System.out.print("Enter vendor name: ");
        this.name = s.nextLine().trim();
        System.out.print("Enter litres: ");
        this.litres = s.nextInt();
        System.out.print("Enter fat reading (%): ");
        this.fatReading = s.nextFloat();
        s.nextLine(); // Clear buffer
        
        this.dateTime = LocalDateTime.now().format(FORMATTER);
        this.price = this.fatReading * 10 * this.litres;
        System.out.printf("✓ %s - %dL - ₹%.2f%n", name, litres, price);
    }
    
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        System.out.print("Enter number of vendors: ");
        int n = s.nextInt();
        s.nextLine();
        
        ArrayList<MilkVendor> vendors = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            System.out.println("\n--- Vendor " + (i + 1) + " ---");
            MilkVendor v = new MilkVendor();
            v.getMilkVendorInfo(s);
            vendors.add(v);
        }
        
        if (saveAllVendors(vendors)) {
            System.out.println("\n✓ ALL DATA SAVED SUCCESSFULLY!");
            showStats();
        } else {
            System.out.println("\n❌ SAVE ERROR!");
        }
        s.close();
    }
    
    private static boolean saveAllVendors(ArrayList<MilkVendor> vendors) {
        File f = new File(FILE);
        int initialCount = getRowCount();
        
        try (PrintWriter w = new PrintWriter(new FileWriter(FILE, true))) {
            if (!f.exists()) w.println(HEADER);
            
            for (int i = 0; i < vendors.size(); i++) {
                MilkVendor v = vendors.get(i);
                String[] dt = v.dateTime.split(" ");
                w.printf("%d,\"%s\",\"%s\",\"%s\",%d,%.1f,%.2f,%.2f%n",
                    initialCount + i + 1, v.name, dt[0], dt[1], 
                    v.litres, v.fatReading, v.fatReading * 10, v.price);
                w.flush();
            }
            
            return getRowCount() == initialCount + vendors.size();
        } catch (IOException e) {
            System.err.println("Save error: " + e.getMessage());
            return false;
        }
    }
    
    private static int getRowCount() {
        try (BufferedReader r = new BufferedReader(new FileReader(FILE))) {
            int count = 0;
            String line;
            boolean skipHeader = true;
            while ((line = r.readLine()) != null) {
                if (skipHeader) { skipHeader = false; continue; }
                if (!line.trim().isEmpty()) count++;
            }
            return count;
        } catch (IOException e) {
            return 0;
        }
    }
    
    private static void showStats() {
        try (BufferedReader r = new BufferedReader(new FileReader(FILE))) {
            int vendors = 0;
            float totalLitres = 0, totalAmount = 0;
            String line;
            r.readLine(); // Skip header
            
            while ((line = r.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] parts = line.split(",");
                    if (parts.length >= 8) {
                        try {
                            vendors++;
                            totalLitres += Float.parseFloat(parts[4]);
                            totalAmount += Float.parseFloat(parts[7]);
                        } catch (NumberFormatException ignored) {}
                    }
                }
            }
            
            System.out.println("\n=== SUMMARY ===");
            System.out.println("Total Vendors: " + vendors);
            System.out.println("Total Litres: " + totalLitres + "L");
            System.out.println("Total Amount: ₹" + totalAmount);
            if (totalLitres > 0) 
                System.out.printf("Avg Price/L: ₹%.2f%n", totalAmount/totalLitres);
                
        } catch (IOException e) {
            System.err.println("Stats error: " + e.getMessage());
        }
    }
}