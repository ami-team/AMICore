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

public class Cloud implements CloudInterface {
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

	public Set<String> getRegions() {

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

				/*--------------*/
				/* IP ADDRESSES */
				/*--------------*/

				String IPv4List = "";
				String IPv6List = "";

				for(Address address : server.getAddresses().values()) {

					/****/ if(address.getVersion() == 4) {
						IPv4List = IPv4List.concat("," + address.getAddr());
					} else if(address.getVersion() == 6) {
						IPv6List = IPv4List.concat("," + address.getAddr());
					}
				}

				if(IPv4List.isEmpty() == false) {
					IPv4List = IPv4List.substring(1);
				}

				if(IPv6List.isEmpty() == false) {
					IPv6List = IPv6List.substring(1);
				}

				/*--------------*/

				result.add(new CloudServer(
					server.getId(),
					server.getName(),
					region,
					server.getFlavor().getId(),
					server.getImage().getId(),
					server.getStatus().toString(),
					IPv4List,
					IPv6List
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

			if(securityGroupApi.isPresent()) {

				for(SecurityGroup securityGroup : securityGroupApi.get().list()) {

					for(SecurityGroupRule securityGroupRule : securityGroup.getRules()) {

						result.add(new CloudSecurityRule(
							region,
							securityGroupRule.getIpProtocol().name(),
							securityGroupRule.getFromPort(),
							securityGroupRule.getToPort(),
							securityGroupRule.getIpRange()
						));
					}
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

	public void softRebootServer(String region, String serverID) {

		ServerApi serverApi = m_novaApi.getServerApi(region);

		serverApi.reboot(serverID, RebootType.SOFT);
	}

	/*---------------------------------------------------------------------*/

	public void hardRebootServer(String region, String serverID) {

		ServerApi serverApi = m_novaApi.getServerApi(region);

		serverApi.reboot(serverID, RebootType.HARD);
	}

	/*---------------------------------------------------------------------*/

	public String buildServer(String region, String name, String flavorID, String imageID, String keypair, String fixedIP, String portUUID, String networUUID) {

		ServerApi serverApi = m_novaApi.getServerApi(region);

		/*-------------*/
		/*   NETWORK   */
		/*-------------*/

		Network.Builder builder =
						Network.builder();

		if(fixedIP != null) {
			builder = builder.fixedIp(fixedIP);
		}

		if(portUUID != null) {
			builder = builder.portUuid(portUUID);
		}

		if(networUUID != null) {
			builder = builder.networkUuid(networUUID);
		}

		ImmutableSet<Network> networks = ImmutableSet.of(
			builder.build()
		);

		/*-------------*/

		CreateServerOptions options = CreateServerOptions.Builder.keyPairName(keypair)
		                                                         .novaNetworks(networks)
		;

		return serverApi.create(name, imageID, flavorID, options).getId();
	}

	/*---------------------------------------------------------------------*/

	public void rebuildServer(String region, String serverID, String imageID) {

		ServerApi serverApi = m_novaApi.getServerApi(region);

		RebuildServerOptions options = RebuildServerOptions.Builder.withImage(imageID);

		serverApi.rebuild(serverID, options);
	}

	/*---------------------------------------------------------------------*/

	public String createImageFromServer(String region, String name, String serverID) {

		ServerApi serverApi = m_novaApi.getServerApi(region);

		return serverApi.createImageFromServer(name, serverID);
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
