LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/LICENSE;md5=4d92cd373abda3937c2bc47fbc49d690"

PV = "${DISTRO_VERSION}"
PR = "r12"
PE = "2"

SRC_URI = "file://lsb_release"

PACKAGES = "${PN}"
PACKAGE_ARCH = "${MACHINE_ARCH}"

def get_layers(d):
    layers = (d.getVar("BBLAYERS", d, 1) or "").split()
    layers_branch_rev = ["%-17s = \"%s:%s\"" % (os.path.basename(i), \
        base_get_metadata_git_branch(i, None).strip().strip('()'), \
        base_get_metadata_git_revision(i, None)) \
        for i in layers]
    i = len(layers_branch_rev)-1
    p1 = layers_branch_rev[i].find("=")
    s1= layers_branch_rev[i][p1:]
    while i > 0:
        p2 = layers_branch_rev[i-1].find("=")
        s2= layers_branch_rev[i-1][p2:]
        if s1 == s2:
            layers_branch_rev[i-1] = layers_branch_rev[i-1][0:p2]
            i -= 1
        else:
            i -= 1
            p1 = layers_branch_rev[i].find("=")
            s1= layers_branch_rev[i][p1:]

    layertext = "Configured Openembedded layers:\n%s\n" % '\n'.join(layers_branch_rev)
    layertext = layertext.replace('<','')
    layertext = layertext.replace('>','')
    return layertext

do_install() {
	install -d ${D}${sysconfdir}
	echo "Yogurt ${DISTRO_VERSION} (${DISTRO_CODENAME})" > ${D}${sysconfdir}/yogurt-version
	echo "Built from branch: ${METADATA_BRANCH}" >> ${D}${sysconfdir}/yogurt-version
	echo "Revision: ${METADATA_REVISION}" >> ${D}${sysconfdir}/yogurt-version
	echo "Target system: ${TARGET_SYS}" >> ${D}${sysconfdir}/yogurt-version

	echo "${@get_layers(d)}" > ${D}${sysconfdir}/yogurt-build-info

	echo "VERSION=\"${DISTRO_VERSION}\"" > ${D}${sysconfdir}/os-release
	echo "NAME=\"Yogurt\"" >> ${D}${sysconfdir}/os-release
	echo "ID=\"yogurt\"" >> ${D}${sysconfdir}/os-release
	echo "PRETTY_NAME=\"The Yogurt Distribution ${DISTRO_VERSION}\"" >> ${D}${sysconfdir}/os-release
	echo "ANSI_COLOR=\"1;35\"" >> ${D}${sysconfdir}/os-release
	echo "HOME_URL=\"http://www.phytec.de\"" >> ${D}${sysconfdir}/os-release

	install -d ${D}${bindir}
	install -m 0755 ${WORKDIR}/lsb_release ${D}${bindir}/
}
RPROVIDES_${PN} = "os-release"
RREPLACES_${PN} = "os-release"
RCONFLICTS_${PN} = "os-release"

