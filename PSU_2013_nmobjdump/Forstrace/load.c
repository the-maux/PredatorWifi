/*
** load.c for  in /why/are/you/watching/my/path
** 
** Made by Maxime renaud
** Login   <root@epitech.net>
** 
** Started on  Tue Mar 11 21:10:23 2014 Maxime renaud
** Last update Sun May 11 21:02:27 2014 Maxime renaud
*/

# include	<fcntl.h>
# include	<stdio.h>
# include	<stdlib.h>
# include	<unistd.h>
# include	<string.h>
# include	<sys/mman.h>
# include	"../../include/my_elf.h"

static t_bool	check_aer(t_elf *elf, t_bool bit)
{
  if (bit == false &&
      (elf->mlen < elf->ehdr_64->e_shoff ||
       elf->ehdr_64->e_shstrndx > elf->mlen ||
       elf->mlen < elf->shdr_64[elf->ehdr_64->e_shstrndx].sh_offset))
    {
      printf("Are you a tronkat-man ?\n");
      return (false);
    }
  else if (bit == true &&
	   (elf->mlen < elf->ehdr->e_shoff ||
	    elf->ehdr->e_shstrndx > elf->mlen ||
	    elf->mlen < elf->shdr[elf->ehdr->e_shstrndx].sh_offset))
    {
      printf("Are you a tronkat-man ?\n");
      return (false);
    }
  return (true);
}

static t_bool	sanyti_64(Elf64_Ehdr *ehdr, char *file, t_elf *elf)
{
  unsigned char	ecx;

  ecx = 0;
  if (ehdr->e_machine != EM_X86_64 && ++ecx)
    printf("%s: Invalid architecture.\n", file);
  if ((ehdr->e_type != ET_EXEC && ehdr->e_type != ET_DYN &&
       ehdr->e_type != ET_REL && ++ecx))
    printf("%s: An unknown type.\n", file);
  if (ehdr->e_version == EV_NONE && ++ecx)
    printf("%s: Invalid version.\n", file);
  if (ehdr->e_ident[EI_CLASS] != ELFCLASS64 && ++ecx)
    printf("%s: Not elf64/x86-64.\n", file);
  elf->bit = false;
  elf->ehdr_64 = ehdr;
  if (elf->maddr + elf->mlen < elf->maddr + elf->ehdr_64->e_shoff && ++ecx)
    printf("%s: ****** Troncate \n", file);
  return ((!ecx) ? true : false);
}

static t_bool	sanyti(Elf32_Ehdr *ehdr, t_elf *elf, char *file)
{
  unsigned char	ecx;

  ecx = 0;
  if (ehdr->e_ident[EI_CLASS] == ELFCLASS32)
    {
      if (ehdr->e_machine != 3 && ++ecx)
	printf("%s: Invalid architecture\n", file);
      else if ((ehdr->e_type != ET_EXEC && ehdr->e_type != ET_DYN &&
	   ehdr->e_type != ET_REL) && ++ecx)
	printf("%s: An unknown type\n", file);
      else if (ehdr->e_version == EV_NONE && ++ecx)
	printf("%s: Invalid version\n", file);
      if (ecx)
	{
	  elf->bit = false;
	  return (false);
	}
      elf->bit = true;
      elf->ehdr = (Elf32_Ehdr *)ehdr;
      if (elf->maddr + elf->mlen < elf->maddr + elf->ehdr->e_shoff && ++ecx)
	printf("%s: ****** Troncate \n", file);
      return (true);
    }
  elf->bit = true;
  return (false);
}

static	t_elf	*init_head(char *filename)
{
  t_elf		*elf;
  int		fd;

  if (((fd = open(filename, O_RDONLY)) == -1) ||
      (!(elf = malloc(sizeof(t_elf)))) ||
      ((elf->maddr = mmap(NULL, lseek(fd, 0, SEEK_END),
			  PROT_READ, MAP_SHARED, fd, 0)) == (void*)-1))
    return ((void*)-1);
  elf->mlen = lseek(fd, 0, SEEK_END);
  close(fd);
  if (sanyti(elf->maddr, elf, filename) == false && elf->bit == true)
    {
      if (sanyti_64(elf->maddr, filename, elf) == false)
	return (NULL);
      elf->shdr_64 = (elf->maddr + elf->ehdr_64->e_shoff);
      elf->shstrtab = elf->maddr + elf->shdr_64
	[elf->ehdr_64->e_shstrndx].sh_offset;
      return (elf);
    }
  else if (elf->bit == false)
    return (NULL);
  elf->shdr = (Elf32_Shdr *)(elf->maddr + elf->ehdr->e_shoff);
  elf->shstrtab = elf->maddr + elf->shdr[elf->ehdr->e_shstrndx].sh_offset;
  return (elf);
}

extern	t_elf	*init(char *filename, t_bool flag, char *bin)
{
  t_elf		*elf;

  if ((elf = init_head(filename)) && elf != (void*)-1)
    {
      if (check_aer(elf, elf->bit) == false)
	return (NULL);
      if (elf->bit == true &&
	  get_symbol_tab_head(elf, -1, elf->shdr) == true)
	return (elf);
      else if (elf->bit == false &&
	       get_symbol_tab_head64(elf, -1, elf->shdr_64) == true)
	return (elf);
      else
	return (NULL);
    }
  printf("%s: '%s': No such file\n", bin, filename);
  return (NULL);
}
