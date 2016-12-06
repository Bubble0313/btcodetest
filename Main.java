import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {

    public static void router_patch_check(String filename)
        throws FileNotFoundException {
        FileInputStream fis = new FileInputStream(filename);
        Scanner in = new Scanner(fis);
        HashMap<String, Integer> hostnames = new HashMap<>();
        HashMap<String, Integer> ips = new HashMap<>();
        List<String> outputs = new LinkedList<>();
        int linenumber = 0;

        while (in.hasNext()) {
            linenumber++;
            String string = in.nextLine();
            if (linenumber > 1) {
                String strs[] = string.split(",");
                if (strs.length == 0) {
                    continue;
                }

                String hostname = strs[0].toLowerCase();
                String ip = strs[1];
                String patched_str = strs[2].toLowerCase();
                String os_version_str = strs[3];

                if (hostnames.containsKey(hostname)) {
                    hostnames.put(hostname, hostnames.get(hostname) + 1);
                } else {
                    hostnames.put(hostname, 1);
                }

                String nums[] = ip.split("\\.");
                if (nums.length != 4) {
                    continue;
                }

                try {
                    for (String num : nums) {
                        int i = Integer.parseInt(num);
                        if (i < 0 || i > 255) {
                            continue;
                        }
                    }
                } catch (NumberFormatException e) {
                    continue;
                }

                if(ips.containsKey(ip)) {
                    ips.put(ip, ips.get(ip) + 1);
                } else {
                    ips.put(ip, 1);
                }

                boolean patched;
                if (patched_str.equals("no")) {
                    patched = false;
                } else if (patched_str.equals("yes")) {
                    patched = true;
                } else {
                    continue;
                }

                float os_version;
                try {
                    os_version = Float.parseFloat(os_version_str);
                } catch (NumberFormatException e) {
                    continue;
                }

                if(os_version >= 12.0 && !patched) {
                    outputs.add(string);
                }
            }
        }
        in.close();

        for(String line : outputs) {
            String strs[] = line.split(",");
            String hostname = strs[0];
            String ip = strs[1];
            String os_version = strs[3];

            if(ips.get(ip) == 1 && hostnames.get(hostname.toLowerCase()) == 1) {
                System.out.print(hostname + " ");
                System.out.print("(" + ip + "), ");
                System.out.print("OS version " + os_version + " ");
                if(strs.length > 4) {
                    System.out.print("[" + strs[4] + "]");
                }
                System.out.println();
            }
        }
    }

    public static void main(String[] args) {
        String filename = args[0];

        try {
            router_patch_check(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
