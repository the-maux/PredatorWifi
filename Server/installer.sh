#!/bin/bash
#---------------------------------------
txtund=$(tput sgr 0 1)
txtbld=$(tput bold)
green=$(tput setaf 2)
bldred=${txtbld}$(tput setaf 1)
red_color=$(tput setaf 1)
color_y=$(tput setaf 3)
bldblu=${txtbld}$(tput setaf 4)
bldwht=${txtbld}$(tput setaf 7)
txtrst=$(tput sgr0)
#---------------------------------------
requeries=true
func_Banner(){
	clear
	echo '   ============================='
	echo "   |$bldblu wifi-pumpkin Installer$txtrst|"
	echo '   ============================='
	echo "          Version: $(tput setaf 5)0.7.5 $txtrst"
	echo "usage: ./installer.sh --install | --uninstall"
}


usage(){
	echo "usage: ./installer.sh --install | --uninstall"
}

func_check_install(){
	if which $1 >/dev/null; then
		echo "----[$green✔$txtrst]----[$green+$txtrst] $1 Installed"
	else
		echo "----[$red_color✘$txtrst]----[$red_color-$txtrst] $1 not Installed "
		nome="$1"
		requeries=$nome
	fi
}

function program_is_installed {
	local return_=1
	type $1 >/dev/null 2>&1 || { local return_=0; }
	echo "$return_"
}

func_install(){
	func_Banner
	if [ "$(id -u)" != "0" ]; then
		echo -e "$(tput setaf 6)[-] This script must be run as root$(tput sgr0)" 1>&2
		exit 1
	fi
	apt-get update
	apt-get install -y python-qt4 python-scapy php5-cli hostapd rfkill
	apt-get install -y python-dev
	apt-get install -y libpcap-dev
    pip install -r requirements.txt
    File="/etc/apt/sources.list"
    if  grep -q '#Wifi Pumpkin' $File;then
	    cp /etc/apt/sources.list.backup /etc/apt/sources.list
	    rm /etc/apt/sources.list.backup
    fi
	echo "----------------------------------------"
	echo "[=]$bldblu checking dependencies $txtrst "
	func_check_install "hostapd"
	func_check_install "dhcpd"
	func_check_install "php"
	echo "----------------------------------------"
	dist=$(tr -s ' \011' '\012' < /etc/issue | head -n 1)
	check_arch=$(uname -m)
	echo "[$green+$txtrst] Distribution Name: $dist"
	if which dhcpd >/dev/null; then
		echo ""
	else
		echo "[$green+$txtrst] dhcpd installer Distribution"
		if [ "$dist" = "Ubuntu" ]; then
			check_dhcp=$(program_is_installed dhcpd)
			if [ $check_dhcp = 0 ]; then
				apt-get install isc-dhcp-server -y
				func_check_install "dhcpd"
			fi
		elif [ "$dist" = "Kali" ]; then
			check_dhcp=$(program_is_installed dhcpd)
			if [ $check_dhcp = 0 ]; then
                cp /etc/apt/sources.list /etc/apt/sources.list.backup
				File="/etc/apt/sources.list"
                if ! grep -q 'deb http://ftp.de.debian.org/debian wheezy main' $File;then
				    echo "deb http://ftp.de.debian.org/debian wheezy main" >> /etc/apt/sources.list
				    apt-get update
				    apt-get install isc-dhcp-server -y
				    cp /etc/apt/sources.list.backup /etc/apt/sources.list
				    rm /etc/apt/sources.list.backup
                fi
                check_dhcp=$(program_is_installed dhcpd)
                if [ $check_dhcp = 0 ]; then
                    if [ "$check_arch" = "i686" ]; then
                        wget http://http.kali.org/kali/pool/main/i/isc-dhcp/isc-dhcp-server_4.3.1-6_i386.deb
                        dpkg -i isc-dhcp-server_4.3.1-6_i386.deb
                    elif [ "$check_arch" = "x86_64" ]; then
                        wget http://http.kali.org/kali/pool/main/i/isc-dhcp/isc-dhcp-server_4.3.1-6_amd64.deb
                        dpkg -i isc-dhcp-server_4.3.1-6_amd64.deb
                    fi
                    rm *.deb
                fi
                func_check_install "dhcpd"
			fi
		fi
	fi
	echo "----------------------------------------"
	echo "[=] $bldblu Install WiFi-Pumpkin $txtrst"
	if [ ! -d "$DIRECTORY" ]; then
		mkdir $DIRECTORY
		cp -r $path_install /usr/share/WiFi-Pumpkin/
		bin_install
		echo "[$green✔$txtrst] wifi-pumpkin installed with success"
		echo "[$green✔$txtrst] execute $bldred sudo wifi-pumpkin$txtrst in terminal"
	else
		rm -r $DIRECTORY
		mkdir $DIRECTORY
		cp -r $path_install /usr/share/WiFi-Pumpkin/
		bin_install
		echo "[$green✔$txtrst] wifi-pumpkin installed with success"
		echo "[$green✔$txtrst] execute $bldred sudo wifi-pumpkin$txtrst in terminal"
	fi
	echo "[$green+$txtrst]$color_y P0cL4bs Team CopyRight 2015-2016$txtrst"
	echo "[$green+$txtrst] Enjoy"
	exit 0
}

bin_install(){
	if [ ! -f "/usr/bin/wifi-pumpkin" ]; then
	    echo "[$green✔$txtrst] PATH::$DIRECTORY"
		echo "[$green✔$txtrst] binary::/usr/bin/"
        ln -sfT /usr/share/WiFi-Pumpkin/wifi-pumpkin.py /usr/bin/wifi-pumpkin
		chmod +x /usr/bin/wifi-pumpkin
	fi
}

uninstall(){
	if [ "$(id -u)" != "0" ]; then
		echo -e "$(tput setaf 6)[-] This script must be run as root$(tput sgr0)" 1>&2
		exit 1
	fi
	if [  -d "$DIRECTORY" ]; then
		echo "[$red_color-$txtrst] Delete Path:$bldwht $DIRECTORY $txtrst"
		rm -r $path_uninstall
		if [ -f "/usr/bin/wifi-pumpkin" ]; then
			rm /usr/bin/wifi-pumpkin
			echo "[$red_color-$txtrst] Deleted Binary:$bldwht/usr/bin/wifi-pumpkin $txtrst"
		fi
	else
		echo "[$red_color✘$txtrst] wifi-pumpkin not Installed"
	fi
}
func_Banner
DIRECTORY="/usr/share/WiFi-Pumpkin"
Dir_isntall=$(pwd)
path_install=$Dir_isntall"/*"
path_uninstall=$DIRECTORY"/"
while [ "$1" != "" ]; do
    case $1 in
        --install )           shift
                                func_install
                                ;;
        --uninstall )    		uninstall
                                ;;
        -h | --help )           usage
                                exit
                                ;;
        * )                     usage
                                exit 1
    esac
    shift
done
