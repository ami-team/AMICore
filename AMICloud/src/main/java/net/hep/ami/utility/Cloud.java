package net.hep.ami.utility;

import java.io.*;
import java.util.*;

import net.hep.ami.CommandSingleton;
import net.hep.ami.jdbc.CatalogSingleton;

import org.jclouds.*;
import org.jclouds.openstack.nova.v2_0.*;
import org.jclouds.openstack.nova.v2_0.domain.*;
import org.jclouds.openstack.nova.v2_0.options.*;
import org.jclouds.openstack.nova.v2_0.features.*;

import com.google.common.io.*;
import com.google.common.collect.*;

public class Cloud implements Closeable {
	/*---------------------------------------------------------------------*/

	public static class CloudServer {

		public String region;
		public String serverID;
		public String serverName;
		public String serverFlavorID;
		public String serverImageID;
		public String serverStatus;
		public String serverIPsv4;
		public String serverIPsv6;

		public CloudServer(String _region, String _serverID, String _serverLabel, String _serverFlavorID, String _serverImageID, String _serverStatus, String _serverIPsv4, String _serverIPsv6) {

			region = _region;
			serverID = _serverID;
			serverName = _serverLabel;
			serverFlavorID = _serverFlavorID;
			serverImageID = _serverImageID;
			serverStatus = _serverStatus;
			serverIPsv4 = _serverIPsv4;
			serverIPsv6 = _serverIPsv6;
		}

		public String toString() {

			return String.format("region: %s, serverID: %s, serverLabel: %s, serverFlavorID: %s, serverImageID: %s, serverStatus: %s, serverIPsv4: %s, serverIPsv6: %s",
				region, serverID, serverName, serverFlavorID, serverImageID, serverStatus, serverIPsv4, serverIPsv6
			);
		}
	}

	/*---------------------------------------------------------------------*/

	public static class CloudFlavor {

		public String region;
		public String flavorID;
		public String flavorLabel;
		public int flavorCPU;
		public int flavorRAM;
		public int flavorDisk;

		public CloudFlavor(String _region, String _flavorID, String _flavorLabel, int _flavorCPU, int _flavorRAM, int _flavorDisk) {

			region = _region;
			flavorID = _flavorID;
			flavorLabel = _flavorLabel;
			flavorCPU = _flavorCPU;
			flavorRAM = _flavorRAM;
			flavorDisk = _flavorDisk;
		}

 		public String toString() {

			return String.format("region: %s, flavorID: %s, flavorLabel: %s, flavorCPU: %d, flavorRAM: %d, flavorDisk: %d",
				region, flavorID, flavorLabel, flavorCPU, flavorRAM, flavorDisk
			);
		}
	}

	/*---------------------------------------------------------------------*/

	public static class CloudImage {

		public String region;
		public String imageID;
		public String imageLabel;
		public int minRam;
		public int minDisk;

		public CloudImage(String _region, String _imageID, String _imageLabel, int _minRam, int _minDisk) {

			region = _region;
			imageID = _imageID;
			imageLabel = _imageLabel;
			minRam = _minRam;
			minDisk = _minDisk;
		}

		public String toString() {

			return String.format("region: %s, imageID: %s, imageLabel: %s, minRam: %d, minDisk: %d",
				region, imageID, imageLabel, minRam, minDisk
			);
		}
	}

	/*---------------------------------------------------------------------*/

	private NovaApi m_novaApi;

	private Set<String> m_regions;

	/*---------------------------------------------------------------------*/

	public Cloud(String endpoint, String identity, String credential) throws Exception {

		m_novaApi = ContextBuilder.newBuilder("openstack-nova")
		                          .endpoint(endpoint)
		                          .credentials(identity, credential)
		                          .buildApi(NovaApi.class)
		;

		m_regions = m_novaApi.getConfiguredRegions();
	}

	/*---------------------------------------------------------------------*/

	public void close() throws IOException {

		Closeables.close(m_novaApi, true);
	}

	/*---------------------------------------------------------------------*/

	public Set<String> listRegions() {

		return m_regions;
	}

	/*---------------------------------------------------------------------*/

	public Set<CloudServer> getServers() {

		Set<CloudServer> result = new HashSet<CloudServer>();

		/*-----------------------------------------------------------------*/

		ServerApi serverApi;

		for(String region : m_regions) {

			serverApi = m_novaApi.getServerApi(region);

			for(Server server : serverApi.listInDetail().concat()) {

				String IPsv4 = "";
				String IPsv6 = "";

				for(Address address : server.getAddresses().values()) {

					/****/ if(address.getVersion() == 4) {
						IPsv4 += "," + address.getAddr();
					} else if(address.getVersion() == 6) {
						IPsv6 += "," + address.getAddr();
					}
				}

				if(IPsv4.isEmpty() == false) {
					IPsv4 = IPsv4.substring(1);
				}

				if(IPsv6.isEmpty() == false) {
					IPsv6 = IPsv6.substring(1);
				}

				result.add(new CloudServer(
					region,
					server.getId(),
					server.getName(),
					server.getFlavor().getId(),
					server.getImage().getId(),
					server.getStatus().toString(),
					IPsv4,
					IPsv6
				));
			}
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public Set<CloudFlavor> getFlavors() {

		Set <CloudFlavor> result = new HashSet<CloudFlavor>();

		/*-----------------------------------------------------------------*/

		FlavorApi flavorApi;

		for(String region : m_regions) {

			flavorApi = m_novaApi.getFlavorApi(region);

			for(Flavor flavor : flavorApi.listInDetail().concat()) {

				result.add(new CloudFlavor(
					region,
					flavor.getId(),
					flavor.getName(),
					flavor.getVcpus(),
					flavor.getRam(),
					flavor.getDisk()
				));
			}
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public Set<CloudImage> getImages() {

		Set <CloudImage> result = new HashSet<CloudImage>();

		/*-----------------------------------------------------------------*/

		ImageApi imageApi;

		for(String region : m_regions) {

			imageApi = m_novaApi.getImageApi(region);

			for(Image image : imageApi.listInDetail().concat()) {

				result.add(new CloudImage(
					region,
					image.getId(),
					image.getName(),
					image.getMinRam(),
					image.getMinDisk()
				));
			}
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public void startServer(String region, String serverID) {

		ServerApi serverApi = m_novaApi.getServerApi(region);

		serverApi.start(serverID);
	}

	/*---------------------------------------------------------------------*/

	public void stopServer(String region, String serverID) {

		ServerApi serverApi = m_novaApi.getServerApi(region);

		serverApi.stop(serverID);
	}

	/*---------------------------------------------------------------------*/

	public void hardRebootServer(String region, String serverID) {

		ServerApi serverApi = m_novaApi.getServerApi(region);

		serverApi.reboot(serverID, RebootType.HARD);
	}

	/*---------------------------------------------------------------------*/

	public void softRebootServer(String region, String serverID) {

		ServerApi serverApi = m_novaApi.getServerApi(region);

		serverApi.reboot(serverID, RebootType.SOFT);
	}

	/*---------------------------------------------------------------------*/

	public String buildServer(
		String region,
		String label,
		String imageID,
		String flavorID,
		String keypair,
		String networUUID,
		String fixedIP
	 ) {
		ServerApi serverApi = m_novaApi.getServerApi(region);

		ImmutableSet<Network> networks = ImmutableSet.of(
			Network.builder().networkUuid(networUUID).fixedIp(fixedIP).build()
		);

		CreateServerOptions options = CreateServerOptions.Builder.keyPairName(keypair)
		                                                         .novaNetworks(networks)
		;

		return serverApi.create(label, imageID, flavorID, options).getId();
	}

	/*---------------------------------------------------------------------*/

	public void rebuildServer(String region, String serverID, String imageID) {

		ServerApi serverApi = m_novaApi.getServerApi(region);

		RebuildServerOptions options = RebuildServerOptions.Builder.withImage(imageID);

		serverApi.rebuild(serverID, options);
	}

	/*---------------------------------------------------------------------*/

	public String createImageFromServer(String region, String imageName, String serverID) {

		ServerApi serverApi = m_novaApi.getServerApi(region);

		return serverApi.createImageFromServer(imageName, serverID);
	}

	/*---------------------------------------------------------------------*/

	public void deleteServer(String region, String serverID) {

		ServerApi serverApi = m_novaApi.getServerApi(region);

		serverApi.delete(serverID);
	}

	/*---------------------------------------------------------------------*/

	public void deleteFlavor(String region, String flavorID) {

		FlavorApi flavorApi = m_novaApi.getFlavorApi(region);

		flavorApi.delete(flavorID);
	}

	/*---------------------------------------------------------------------*/

	public void deleteImage(String region, String imageID) {

		ImageApi imageApi = m_novaApi.getImageApi(region);

		imageApi.delete(imageID);
	}

	/*---------------------------------------------------------------------*/

	public static void main(String[] args) throws Exception {

		Map<String, String> arguments = new HashMap<String, String>();

		try {
			/*System.out.println(*/CatalogSingleton.listCatalogs()/*)*/;

			arguments.put("endpoint", "https://cckeystone.in2p3.fr:5000/v2.0/");
			arguments.put("identity", "ami:jfulachi");
			arguments.put("credential", "22a6d2bb-395d-4267-92c5-adf3b57ec034");

			System.out.println(CommandSingleton.executeCommand("CloudListServers", arguments).replace(">", ">\n"));

		} catch(Exception e) {
			System.out.println(e.getMessage());
		}

		System.exit(0);
	}

	/*---------------------------------------------------------------------*/
}
