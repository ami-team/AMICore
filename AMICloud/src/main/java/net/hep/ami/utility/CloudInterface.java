package net.hep.ami.utility;

import java.io.*;
import java.util.*;

public interface CloudInterface extends Closeable {
	/*---------------------------------------------------------------------*/

	public static class CloudServer {

		public String ID;
		public String name;
		public String region;
		public String flavorID;
		public String imageID;
		public String status;
		public String IPv4List;
		public String IPv6List;

		public CloudServer(String _ID, String _name, String _region, String _flavorID, String _imageID, String _status, String _IPv4List, String _IPv6List) {

			ID = _ID;
			name = _name;
			region = _region;
			flavorID = _flavorID;
			imageID = _imageID;
			status = _status;
			IPv4List = _IPv4List;
			IPv6List = _IPv6List;
		}

		public String toString() {

			return String.format("ID: %s, name: %s, region: %s, flavorID: %s, imageID: %s, status: %s, IPv4List: %s, IPv6List: %s",
				ID, name, region, flavorID, imageID, status, IPv4List, IPv6List
			);
		}
	}

	/*---------------------------------------------------------------------*/

	public static class CloudFlavor {

		public String ID;
		public String name;
		public String region;
		public int cpu;
		public int ram;
		public int disk;

		public CloudFlavor(String _ID, String _name, String _region, int _cpu, int _ram, int _disk) {

			ID = _ID;
			name = _name;
			region = _region;
			cpu = _cpu;
			ram = _ram;
			disk = _disk;
		}

 		public String toString() {

			return String.format("ID: %s, name: %s, region: %s, cpu: %d, ram: %d, disk: %d",
				ID, name, region, cpu, ram, disk
			);
		}
	}

	/*---------------------------------------------------------------------*/

	public static class CloudImage {

		public String ID;
		public String name;
		public String region;
		public int minRam;
		public int minDisk;

		public CloudImage(String _ID, String _name, String _region, int _minRam, int _minDisk) {

			ID = _ID;
			name = _name;
			region = _region;
			minRam = _minRam;
			minDisk = _minDisk;
		}

		public String toString() {

			return String.format("ID: %s, name: %s, region: %s, minRam: %d, minDisk: %d",
				ID, name, region, minRam, minDisk
			);
		}
	}

	/*---------------------------------------------------------------------*/

	public static class CloudSecurityRule {

		public String region;
		public String protocol;
		public int fromPort;
		public int toPort;
		public String IPRange;

		public CloudSecurityRule(String _region, String _protocol, int _fromPort, int _toPort, String _IPRange) {

			region = _region;
			protocol = _protocol;
			fromPort = _fromPort;
			toPort = _toPort;
			IPRange = _IPRange;
		}

		public String toString() {

			return String.format("region: %s, protocol: %s, fromPort: %d, toPort: %d, IPRange: %d",
					region, protocol, fromPort, toPort, IPRange
			);
		}
	}

	/*---------------------------------------------------------------------*/

//	public CloudInterface(String endpoint, String identity, String credential);

	/*---------------------------------------------------------------------*/

	public Set<String> getRegions();

	/*---------------------------------------------------------------------*/

	public Set<CloudServer> getServers();

	public Set<CloudFlavor> getFlavors();

	public Set<CloudImage> getImages();

	public Set<CloudSecurityRule> getSecurityRules();

	/*---------------------------------------------------------------------*/

	public void startServer(String region, String serverID);

	public void stopServer(String region, String serverID);

	/*---------------------------------------------------------------------*/

	public void softRebootServer(String region, String serverID);

	public void hardRebootServer(String region, String serverID);

	/*---------------------------------------------------------------------*/

	public String buildServer(String region, String name, String flavorID, String imageID, String keypair, String fixedIP, String portUUID, String networUUID);

	public void rebuildServer(String region, String serverID, String imageID);

	public String createImageFromServer(String region, String name, String serverID);

	/*---------------------------------------------------------------------*/

	public void deleteServer(String region, String serverID);

	public void deleteFlavor(String region, String flavorID);

	public void deleteImage(String region, String imageID);

	/*---------------------------------------------------------------------*/
}
