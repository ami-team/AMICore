package net.hep.ami.utility;

import java.io.*;
import java.util.*;

import org.jclouds.*;
import org.jclouds.openstack.nova.v2_0.*;
import org.jclouds.openstack.nova.v2_0.domain.*;
import org.jclouds.openstack.nova.v2_0.options.*;
import org.jclouds.openstack.nova.v2_0.features.*;
import org.jclouds.openstack.nova.v2_0.extensions.*;

import com.google.common.io.*;
import com.google.common.collect.*;

public class Cloud implements Closeable {
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
					server.getId(),
					server.getName(),
					region,
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

		Set<CloudFlavor> result = new HashSet<CloudFlavor>();

		/*-----------------------------------------------------------------*/

		FlavorApi flavorApi;

		for(String region : m_regions) {

			flavorApi = m_novaApi.getFlavorApi(region);

			for(Flavor flavor : flavorApi.listInDetail().concat()) {

				result.add(new CloudFlavor(
					flavor.getId(),
					flavor.getName(),
					region,
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
					image.getId(),
					image.getName(),
					region,
					image.getMinRam(),
					image.getMinDisk()
				));
			}
		}

		/*-----------------------------------------------------------------*/

		return result;
	}

	/*---------------------------------------------------------------------*/

	public Set<CloudSecurityRule> getSecurityRules() {

		Set<CloudSecurityRule> result = new HashSet<CloudSecurityRule>();

		/*-----------------------------------------------------------------*/

		com.google.common.base.Optional<SecurityGroupApi> securityGroupApi;

		for(String region : m_regions) {

			securityGroupApi = m_novaApi.getSecurityGroupApi(region);

			if(securityGroupApi.isPresent()) for(SecurityGroup securityGroup: securityGroupApi.get().list()) {

				for(SecurityGroupRule rule : securityGroup.getRules()) {

					result.add(new CloudSecurityRule(
						region,
						rule.getIpProtocol().name(),
						rule.getFromPort(),
						rule.getToPort(),
						rule.getIpRange()
					));
				}
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
}
